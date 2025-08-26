package com.example.back.controller;

import com.example.back.entity.LeaveApplication;
import com.example.back.repository.LeaveApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leave-applications")
public class LeaveApplicationController {

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @GetMapping
    public List<LeaveApplication> getAllLeaveApplications() {
        return leaveApplicationRepository.findAll();
    }

    @GetMapping("/all")
    public List<LeaveApplication> getAllLeaveApplicationsEndpoint() {
        return leaveApplicationRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveApplication> getLeaveApplicationById(@PathVariable Long id) {
        Optional<LeaveApplication> leaveApplication = leaveApplicationRepository.findById(id);
        return leaveApplication.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/staff/{staffId}")
    public List<LeaveApplication> getLeaveApplicationsByStaffId(@PathVariable Long staffId) {
        return leaveApplicationRepository.findByStaffId(staffId);
    }

    @PostMapping
    public LeaveApplication createLeaveApplication(@RequestBody LeaveApplication leaveApplication) {
        leaveApplication.setIsRead(false); // Initialize isRead to false
        return leaveApplicationRepository.save(leaveApplication);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveApplication> updateLeaveApplication(@PathVariable Long id, @RequestBody LeaveApplication leaveApplicationDetails) {
        Optional<LeaveApplication> leaveApplication = leaveApplicationRepository.findById(id);
        if (leaveApplication.isPresent()) {
            LeaveApplication existingLeaveApplication = leaveApplication.get();
            existingLeaveApplication.setStaffId(leaveApplicationDetails.getStaffId());
            existingLeaveApplication.setStartDate(leaveApplicationDetails.getStartDate());
            existingLeaveApplication.setEndDate(leaveApplicationDetails.getEndDate());
            existingLeaveApplication.setReason(leaveApplicationDetails.getReason());
            existingLeaveApplication.setStatus(leaveApplicationDetails.getStatus());
            return ResponseEntity.ok(leaveApplicationRepository.save(existingLeaveApplication));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveApplication(@PathVariable Long id) {
        if (leaveApplicationRepository.existsById(id)) {
            leaveApplicationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/mark-as-read")
    public ResponseEntity<LeaveApplication> markLeaveApplicationAsRead(@PathVariable Long id) {
        Optional<LeaveApplication> leaveApplication = leaveApplicationRepository.findById(id);
        if (leaveApplication.isPresent()) {
            LeaveApplication existingLeaveApplication = leaveApplication.get();
            // 允许已拒绝 (2) 的请假申请被标记为已读
            existingLeaveApplication.setIsRead(true);
            leaveApplicationRepository.save(existingLeaveApplication);
            // 如果是已拒绝状态的申请，标记为已读后自动删除
            if (existingLeaveApplication.getStatus() == 2) {
                leaveApplicationRepository.delete(existingLeaveApplication);
                return ResponseEntity.ok().build(); // 删除成功返回200 OK
            }
            return ResponseEntity.ok(existingLeaveApplication);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveApplication> approveLeaveApplication(@PathVariable Long id) {
        Optional<LeaveApplication> leaveApplication = leaveApplicationRepository.findById(id);
        if (leaveApplication.isPresent()) {
            LeaveApplication existingLeaveApplication = leaveApplication.get();
            existingLeaveApplication.setStatus(1); // 1 for approved
            return ResponseEntity.ok(leaveApplicationRepository.save(existingLeaveApplication));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveApplication> rejectLeaveApplication(@PathVariable Long id) {
        Optional<LeaveApplication> leaveApplication = leaveApplicationRepository.findById(id);
        if (leaveApplication.isPresent()) {
            LeaveApplication existingLeaveApplication = leaveApplication.get();
            existingLeaveApplication.setStatus(2); // 2 for rejected
            return ResponseEntity.ok(leaveApplicationRepository.save(existingLeaveApplication));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pending")
    public List<LeaveApplication> getPendingLeaveApplications() {
        return leaveApplicationRepository.findByStatus(0); // 0 for pending
    }
}