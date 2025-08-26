package com.example.back.controller;

import com.example.back.entity.Attendance;
import com.example.back.entity.LeaveApplication;
import com.example.back.repository.AttendanceRepository;
import com.example.back.repository.LeaveApplicationRepository;
import com.example.back.repository.StaffRepository;
import com.example.back.dto.AttendanceStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private StaffRepository staffRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping
    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    @GetMapping("/date")
    public List<Attendance> getAttendancesByDate(@RequestParam String date) {
        System.out.println("查询日期: " + date);
        List<Attendance> attendances = attendanceRepository.findById_CheckDate(date);
        System.out.println("查询结果: " + attendances.size() + " 条记录");
        for (Attendance attendance : attendances) {
            System.out.println("考勤记录 - 员工ID: " + attendance.getId().getStaffId() + ", 日期: " + attendance.getId().getCheckDate());
        }
        return attendances;
    }

    @GetMapping("/employee/{staffId}")
    public List<Attendance> getAttendancesByEmployeeId(@PathVariable Long staffId) {
        return attendanceRepository.findById_StaffId(staffId);
    }

    @GetMapping("/status/{staffId}/{date}")
    public ResponseEntity<AttendanceStatusDTO> getAttendanceStatusForDate(
            @PathVariable Long staffId,
            @PathVariable String date) {
        try {
            Date checkDate = dateFormat.parse(date);

            // Check for attendance record
            Optional<Attendance> attendance = attendanceRepository.findById_StaffIdAndId_CheckDate(staffId, date);
            if (attendance.isPresent()) {
                String staffName = staffRepository.findById(staffId).map(staff -> staff.getUsername()).orElse("未知员工");
                return ResponseEntity.ok(new AttendanceStatusDTO(staffId, staffName, checkDate, 1, null));
            }

            // Check for approved leave application
            List<LeaveApplication> approvedLeaves = leaveApplicationRepository.findByStaffIdAndStatus(staffId, 1);
            boolean onLeave = approvedLeaves.stream().anyMatch(leave ->
                    !checkDate.before(leave.getStartDate()) && !checkDate.after(leave.getEndDate())
            );

            if (onLeave) {
                // Find the specific leave application for the date to get the reason
                Optional<LeaveApplication> currentLeave = approvedLeaves.stream()
                        .filter(leave -> !checkDate.before(leave.getStartDate()) && !checkDate.after(leave.getEndDate()))
                        .findFirst();
                String leaveReason = currentLeave.map(LeaveApplication::getReason).orElse(null);
                String staffName = staffRepository.findById(staffId).map(staff -> staff.getUsername()).orElse("未知员工");
                return ResponseEntity.ok(new AttendanceStatusDTO(staffId, staffName, checkDate, 2, leaveReason));
            }

            // If no attendance and not on leave, then "未签到"
            String staffName = staffRepository.findById(staffId).map(staff -> staff.getUsername()).orElse("未知员工");
            return ResponseEntity.ok(new AttendanceStatusDTO(staffId, staffName, checkDate, 0, null));

        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/all/{date}")
    public List<AttendanceStatusDTO> getAllEmployeesAttendanceStatusForDate(@PathVariable String date) {
        List<AttendanceStatusDTO> statuses = new ArrayList<>();
        try {
            Date checkDate = dateFormat.parse(date);

            // Get all employees (assuming you have an EmployeeRepository or similar)
            // For simplicity, let's get all staff IDs from attendance records or leave applications
            // In a real app, you'd fetch all active employees
            List<Long> allStaffIds = attendanceRepository.findAll().stream()
                    .map(att -> att.getId().getStaffId())
                    .distinct()
                    .collect(Collectors.toList());

            // Add staff from leave applications who might not have attendance records
            leaveApplicationRepository.findAll().stream()
                    .map(LeaveApplication::getStaffId)
                    .distinct()
                    .forEach(staffId -> {
                        if (!allStaffIds.contains(staffId)) {
                            allStaffIds.add(staffId);
                        }
                    });

            for (Long staffId : allStaffIds) {
                Optional<Attendance> attendance = attendanceRepository.findById_StaffIdAndId_CheckDate(staffId, date);
                if (attendance.isPresent()) {
                    String staffName = staffRepository.findById(staffId).map(staff -> staff.getUsername()).orElse("未知员工");
                    statuses.add(new AttendanceStatusDTO(staffId, staffName, checkDate, 1, null));
                } else {
                    List<LeaveApplication> approvedLeaves = leaveApplicationRepository.findByStaffIdAndStatus(staffId, 1);
                    boolean onLeave = approvedLeaves.stream().anyMatch(leave ->
                            !checkDate.before(leave.getStartDate()) && !checkDate.after(leave.getEndDate())
                    );
                    if (onLeave) {
                        Optional<LeaveApplication> currentLeave = approvedLeaves.stream()
                                .filter(leave -> !checkDate.before(leave.getStartDate()) && !checkDate.after(leave.getEndDate()))
                                .findFirst();
                        String leaveReason = currentLeave.map(LeaveApplication::getReason).orElse(null);
                        String staffName = staffRepository.findById(staffId).map(staff -> staff.getUsername()).orElse("未知员工");
                        statuses.add(new AttendanceStatusDTO(staffId, staffName, checkDate, 2, leaveReason));
                    } else {
                        String staffName = staffRepository.findById(staffId).map(staff -> staff.getUsername()).orElse("未知员工");
                    statuses.add(new AttendanceStatusDTO(staffId, staffName, checkDate, 0, null));
                    }
                }
            }
        } catch (ParseException e) {
            // Handle parse exception
        }
        return statuses;
    }
}