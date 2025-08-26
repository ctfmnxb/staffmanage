package com.example.back.controller;

import com.example.back.entity.DailyCheckin;
import com.example.back.entity.Staff;
import com.example.back.repository.DailyCheckinRepository;
import com.example.back.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/daily-checkin")
public class DailyCheckinController {

    @Autowired
    private DailyCheckinRepository dailyCheckinRepository;

    @Autowired
    private StaffRepository staffRepository;

    @PostMapping
    public ResponseEntity<?> checkIn(@RequestBody Map<String, Object> payload) {
        Object staffIdObj = payload.get("staffId");
        if (staffIdObj == null) {
            return ResponseEntity.badRequest().body("Staff ID cannot be null.");
        }
        Long staffId = ((Number) staffIdObj).longValue();
        String checkDateStr = (String) payload.get("checkDate");
        LocalDate checkDate = LocalDate.parse(checkDateStr);

        if (dailyCheckinRepository.existsByStaffIdAndCheckDate(staffId, checkDate)) {
            return ResponseEntity.badRequest().body("Already checked in for this date.");
        }

        DailyCheckin dailyCheckin = new DailyCheckin();
        dailyCheckin.setStaffId(staffId);
        dailyCheckin.setCheckDate(checkDate);
        dailyCheckinRepository.save(dailyCheckin);
        return ResponseEntity.ok("Check-in successful!");
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<List<Map<String, Object>>> getCheckInRecords(@PathVariable Long staffId) {
        List<DailyCheckin> records = dailyCheckinRepository.findByStaffId(staffId);
        List<Map<String, Object>> result = records.stream().map(record -> {
            Map<String, Object> recordMap = new java.util.HashMap<>();
            recordMap.put("checkDate", record.getCheckDate());
            recordMap.put("staffId", record.getStaffId());
            Optional<Staff> staffOptional = staffRepository.findById(record.getStaffId());
            staffOptional.ifPresent(staff -> recordMap.put("staffName", staff.getUsername()));
            return recordMap;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/date")
    public ResponseEntity<List<DailyCheckin>> getCheckInRecordsByDate(@RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        List<DailyCheckin> records = dailyCheckinRepository.findByCheckDate(date);
        return ResponseEntity.ok(records);
    }
}