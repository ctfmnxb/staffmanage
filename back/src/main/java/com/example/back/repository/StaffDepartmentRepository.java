package com.example.back.repository;

import com.example.back.entity.StaffDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

@Repository

public interface StaffDepartmentRepository extends JpaRepository<StaffDepartment, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM StaffDepartment sd WHERE sd.deptId = :deptId")
    void deleteByDeptId(Integer deptId);

    Optional<StaffDepartment> findByStaffId(Long staffId);

    List<StaffDepartment> findByDeptId(Integer deptId);

    Optional<StaffDepartment> findByStaffIdAndDeptId(Long staffId, Integer deptId);
}