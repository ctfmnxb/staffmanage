package com.example.back.controller;

import com.example.back.entity.Salary;
import com.example.back.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    @Autowired
    private SalaryRepository salaryRepository;

    @GetMapping
    public List<Salary> getAllSalaries() {
        return salaryRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Salary> getSalaryById(@PathVariable Long id) {
        Optional<Salary> salary = salaryRepository.findById(id);
        return salary.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/staff/{staffId}")
    public List<Salary> getSalariesByStaffId(@PathVariable Long staffId) {
        return salaryRepository.findByStaffId(staffId);
    }

    @PostMapping
    public Salary createSalary(@RequestBody Salary salary) {
        salary.setCreatedAt(new java.util.Date()); // 设置创建时间
        return salaryRepository.save(salary);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Salary> updateSalary(@PathVariable Long id, @RequestBody Salary salaryDetails) {
        Optional<Salary> salary = salaryRepository.findById(id);
        if (salary.isPresent()) {
            Salary existingSalary = salary.get();
            existingSalary.setStaffId(salaryDetails.getStaffId());
            existingSalary.setDate(salaryDetails.getDate());
            existingSalary.setBaseSalary(salaryDetails.getBaseSalary());
            existingSalary.setBonus(salaryDetails.getBonus());
            existingSalary.setDeduction(salaryDetails.getDeduction());
            existingSalary.setTotalSalary(salaryDetails.getTotalSalary());
            existingSalary.setCreatedAt(salaryDetails.getCreatedAt());
            return ResponseEntity.ok(salaryRepository.save(existingSalary));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        if (salaryRepository.existsById(id)) {
            salaryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}