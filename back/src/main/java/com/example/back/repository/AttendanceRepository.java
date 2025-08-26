package com.example.back.repository;

import com.example.back.entity.Attendance;
import com.example.back.entity.AttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {
    List<Attendance> findById_StaffId(Long staffId);
    List<Attendance> findById_CheckDate(String checkDate);
    Optional<Attendance> findById_StaffIdAndId_CheckDate(Long staffId, String checkDate);
}