package com.example.back.repository;

import com.example.back.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByStaffId(Long staffId);
}