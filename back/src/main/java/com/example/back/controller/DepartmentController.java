package com.example.back.controller;

import com.example.back.entity.Department;
import com.example.back.repository.DepartmentRepository;
import com.example.back.repository.StaffDepartmentRepository;
import com.example.back.repository.StaffRepository;
import com.example.back.entity.StaffDepartment;
import com.example.back.entity.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StaffDepartmentRepository staffDepartmentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @GetMapping
    public List<Department> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        for (Department department : departments) {
            if (department.getManagerId() != null) {
                staffRepository.findById(department.getManagerId())
                        .ifPresentOrElse(
                                staff -> department.setManagerName(staff.getUsername()),
                                () -> department.setManagerName("无") // 如果找不到经理，设置为“无”
                        );
            } else {
                department.setManagerName("无"); // 如果 managerId 为 null，设置为“无”
            }
        }
        return departments;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Integer id) {
        Optional<Department> department = departmentRepository.findById(id);
        return department.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        // 检查部门名称是否已存在
        if (departmentRepository.findByName(department.getName()).isPresent()) {
            return ResponseEntity.badRequest().build(); // 或者返回更具体的错误信息
        }

        department.setCreatedAt(LocalDateTime.now());
        // 确保 managerId 不为 null，如果前端没有提供，则设置为 null
        if (department.getManagerId() == null) {
            department.setManagerId(null);
        }
        System.out.println("Before saving department: " + department.getId());
        Department savedDepartment = departmentRepository.save(department);
        System.out.println("After saving department: " + savedDepartment.getId());



        return ResponseEntity.ok(savedDepartment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Integer id, @RequestBody Department departmentDetails) {
        Optional<Department> department = departmentRepository.findById(id);
        if (department.isPresent()) {
            Department existingDepartment = department.get();
            existingDepartment.setName(departmentDetails.getName());
            existingDepartment.setManagerId(departmentDetails.getManagerId());
            return ResponseEntity.ok(departmentRepository.save(existingDepartment));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Integer id) {
        if (departmentRepository.existsById(id)) {
            // 先删除 staff_department 表中与该部门相关的记录
            staffDepartmentRepository.deleteByDeptId(id);
            departmentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{deptId}/members")
    public ResponseEntity<List<Map<String, Object>>> getDepartmentMembers(@PathVariable Integer deptId) {
        Optional<Department> departmentOptional = departmentRepository.findById(deptId);
        if (departmentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<StaffDepartment> staffDepartments = staffDepartmentRepository.findByDeptId(deptId);
        List<Map<String, Object>> members = new java.util.ArrayList<>();

        for (StaffDepartment sd : staffDepartments) {
            Optional<Staff> staffOptional = staffRepository.findById(sd.getStaffId());
            staffOptional.ifPresent(staff -> {
                Map<String, Object> memberInfo = new java.util.HashMap<>();
                memberInfo.put("staffId", staff.getId()); // 确保这里是 staffId
                memberInfo.put("username", staff.getUsername());
                boolean isManager = sd.getIsManager() != null && sd.getIsManager();
                // 额外校验：如果 staff_department 中标记为部长，但 department 表中 managerId 不匹配，则纠正
                if (isManager && (departmentOptional.get().getManagerId() == null || !departmentOptional.get().getManagerId().equals(staff.getId()))) {
                    isManager = false;
                }
                memberInfo.put("isManager", isManager);
                members.add(memberInfo);
            });
        }
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{deptId}/add-member")
    public ResponseEntity<String> addDepartmentMember(@PathVariable Integer deptId, @RequestBody Map<String, Long> payload) {
        Long staffId = payload.get("staffId");

        if (staffId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("员工ID不能为空。");
        }

        Optional<Department> departmentOptional = departmentRepository.findById(deptId);
        if (departmentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("部门未找到。");
        }

        Optional<Staff> staffOptional = staffRepository.findById(staffId);
        if (staffOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("员工未找到。");
        }

        // 检查员工是否已在 staff_department 表中
        Optional<StaffDepartment> existingStaffDepartment = staffDepartmentRepository.findByStaffId(staffId);

        StaffDepartment staffDepartment;
        if (existingStaffDepartment.isPresent()) {
            // 如果员工已存在，更新其部门ID
            staffDepartment = existingStaffDepartment.get();
            staffDepartment.setDeptId(deptId);
            staffDepartment.setIsManager(false); // 默认设置为非部长
        } else {
            // 如果员工不存在，创建新的记录
            staffDepartment = new StaffDepartment();
            staffDepartment.setStaffId(staffId);
            staffDepartment.setDeptId(deptId);
            staffDepartment.setIsManager(false); // 默认设置为非部长
        }

        staffDepartmentRepository.save(staffDepartment);
        return ResponseEntity.ok("员工成功加入部门。");
    }

    @PostMapping("/{deptId}/set-manager")
    public ResponseEntity<String> setDepartmentManager(@PathVariable Integer deptId, @RequestBody Map<String, Long> payload) {
        Long staffId = payload.get("staffId");

        if (staffId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("员工ID不能为空。");
        }

        Optional<Department> departmentOptional = departmentRepository.findById(deptId);
        if (departmentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("部门未找到。");
        }

        Optional<Staff> staffOptional = staffRepository.findById(staffId);
        if (staffOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("员工未找到。");
        }

        Department department = departmentOptional.get();

        // 检查该部门是否已有部长
        if (department.getManagerId() != null) {
            // 如果有现有部长，将其isManager字段设置为false
            staffDepartmentRepository.findByStaffIdAndDeptId(Long.valueOf(department.getManagerId()), deptId)
                    .ifPresent(sd -> {
                        sd.setIsManager(false);
                        staffDepartmentRepository.save(sd);
                    });
        }

        // 将指定员工设为部长
        Optional<StaffDepartment> staffDepartmentOptional = staffDepartmentRepository.findByStaffIdAndDeptId(staffId, deptId);
        if (staffDepartmentOptional.isPresent()) {
            StaffDepartment staffDepartment = staffDepartmentOptional.get();
            staffDepartment.setIsManager(true);
            staffDepartmentRepository.save(staffDepartment);

            // 更新部门表中的managerId
            department.setManagerId(staffId);
            departmentRepository.save(department);

            return ResponseEntity.ok("设置部长成功。");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("该员工不属于此部门，无法设为部长。");
        }
    }

    @PostMapping("/{deptId}/remove-member")
    public ResponseEntity<String> removeDepartmentMember(@PathVariable Integer deptId, @RequestBody Map<String, Long> payload) {
        Long staffId = payload.get("staffId");

        if (staffId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("员工ID不能为空。");
        }

        Optional<Department> departmentOptional = departmentRepository.findById(deptId);
        if (departmentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("部门未找到。");
        }

        Optional<StaffDepartment> staffDepartmentOptional = staffDepartmentRepository.findByStaffIdAndDeptId(staffId, deptId);
        if (staffDepartmentOptional.isPresent()) {
            StaffDepartment staffDepartment = staffDepartmentOptional.get();
            staffDepartmentRepository.delete(staffDepartment);

            // 如果被删除的员工是该部门的部长，需要将Department表中的managerId设置为null
            Department department = departmentOptional.get();
            if (department.getManagerId() != null && department.getManagerId().equals(staffId)) {
                department.setManagerId(null);
                departmentRepository.save(department);
            }
            return ResponseEntity.ok("员工已从部门中移除。");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("该员工不属于此部门。");
        }
    }

    @PostMapping("/{deptId}/unset-manager")
    public ResponseEntity<String> unsetDepartmentManager(@PathVariable Integer deptId, @RequestBody Map<String, Long> payload) {
        Long staffId = payload.get("staffId");

        if (staffId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("员工ID不能为空。");
        }

        Optional<Department> departmentOptional = departmentRepository.findById(deptId);
        if (departmentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("部门未找到。");
        }

        Optional<StaffDepartment> staffDepartmentOptional = staffDepartmentRepository.findByStaffIdAndDeptId(staffId, deptId);
        if (staffDepartmentOptional.isPresent()) {
            StaffDepartment staffDepartment = staffDepartmentOptional.get();
            if (staffDepartment.getIsManager() != null && staffDepartment.getIsManager()) {
                staffDepartment.setIsManager(false);
                staffDepartmentRepository.save(staffDepartment);

                // 将Department表中的managerId设置为null
                Department department = departmentOptional.get();
                if (department.getManagerId() != null && department.getManagerId().equals(staffId)) {
                    department.setManagerId(null);
                    departmentRepository.save(department);
                }
                return ResponseEntity.ok("取消部长成功。");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("该员工不是部长。");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("该员工不属于此部门。");
        }
    }
}