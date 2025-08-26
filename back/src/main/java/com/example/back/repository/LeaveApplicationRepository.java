package com.example.back.repository;

import com.example.back.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByStaffId(Long staffId);
    List<LeaveApplication> findByStatus(Integer status);
    List<LeaveApplication> findByStaffIdAndStatus(Long staffId, Integer status);
}