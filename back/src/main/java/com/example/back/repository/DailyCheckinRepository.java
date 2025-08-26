package com.example.back.repository;

import com.example.back.entity.DailyCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyCheckinRepository extends JpaRepository<DailyCheckin, DailyCheckin.DailyCheckinId> {
    List<DailyCheckin> findByStaffId(Long staffId);
    boolean existsByStaffIdAndCheckDate(Long staffId, LocalDate checkDate);
    List<DailyCheckin> findByCheckDate(LocalDate checkDate);
    int countByStaffId(Long staffId);
}