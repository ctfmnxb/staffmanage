package com.example.back.controller;

import com.example.back.entity.Staff;
import com.example.back.entity.Department;
import com.example.back.entity.StaffDepartment;
import com.example.back.repository.DepartmentRepository;
import com.example.back.repository.StaffDepartmentRepository;
import com.example.back.repository.StaffRepository;
import com.example.back.repository.DailyCheckinRepository;
import com.example.back.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

@RestController
public class StaffController {
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private DailyCheckinRepository dailyCheckinRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private StaffDepartmentRepository staffDepartmentRepository;
    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/api/staff/register")
    public Map<String, Object> register(@RequestBody Map<String, String> user) {
        Map<String, Object> response = new HashMap<>();
        String username = user.get("username");
        String password = user.get("password");

        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            if (staffRepository.findByUsername(username) != null) {
                response.put("success", false);
                response.put("message", "用户名已存在");
            } else {
                Staff staff = new Staff(username, password);
                staffRepository.save(staff);
                response.put("success", true);
                response.put("message", "注册成功");
            }
        } else {
            response.put("success", false);
            response.put("message", "用户名和密码不能为空");
        }
        return response;
    }

    @PostMapping("/api/staff/login")
    public ResponseEntity<Staff> login(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String password = user.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().build();
        }

        Staff existingStaff = staffRepository.findByUsername(username);
        if (existingStaff != null && existingStaff.getPassword().equals(password)) {
            Optional<StaffDepartment> staffDepartment = staffDepartmentRepository.findByStaffId(existingStaff.getId());
            if (staffDepartment.isPresent()) {
                Optional<Department> department = departmentRepository.findById(staffDepartment.get().getDeptId());
                department.ifPresent(d -> {
                    existingStaff.setDepartmentId(d.getId());
                    existingStaff.setDepartmentName(d.getName());
                    if (d.getManagerId() != null) {
                        staffRepository.findById(d.getManagerId()).ifPresent(manager -> existingStaff.setDepartmentManagerName(manager.getUsername()));
                    } else {
                        existingStaff.setDepartmentManagerName("无");
                    }
                    existingStaff.setIsManager(staffDepartment.get().getIsManager());
                });
            } else {
                existingStaff.setDepartmentId(null);
                existingStaff.setDepartmentName(null);
                existingStaff.setDepartmentManagerName(null);
                existingStaff.setIsManager(false);
            }
            return ResponseEntity.ok(existingStaff);
        } else {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
    }

    @PutMapping("/api/staff/change-password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String username = request.get("username");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (username == null || username.isEmpty() || oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            response.put("success", false);
            response.put("message", "用户名、旧密码和新密码不能为空");
            return response;
        }

        Staff existingStaff = staffRepository.findByUsername(username);
        if (existingStaff == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return response;
        }

        if (!existingStaff.getPassword().equals(oldPassword)) {
            response.put("success", false);
            response.put("message", "旧密码不正确");
            return response;
        }

        existingStaff.setPassword(newPassword);
        staffRepository.save(existingStaff);

        response.put("success", true);
        response.put("message", "密码修改成功");
        return response;
    }

    @GetMapping("/api/staffs")
    public List<Staff> getAllStaff() {
        List<Staff> staffs = staffRepository.findAll();
        for (Staff staff : staffs) {
            int attendanceDays = dailyCheckinRepository.countByStaffId(staff.getId());
            staff.setAttendanceDays(attendanceDays);
        }
        return staffs;
    }

    @GetMapping("/api/staffs/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable Long id) {
        return staffRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/staff/current")
    public ResponseEntity<Staff> getCurrentStaff(@RequestParam Long staffId) {
        Optional<Staff> staffOptional = staffRepository.findById(staffId);

        if (staffOptional.isPresent()) {
            Staff staff = staffOptional.get();

            Optional<StaffDepartment> staffDepartment = staffDepartmentRepository.findByStaffId(staff.getId());
            if (staffDepartment.isPresent()) {
                Optional<Department> department = departmentRepository.findById(staffDepartment.get().getDeptId());
                department.ifPresent(d -> {
                    staff.setDepartmentId(d.getId());
                    staff.setDepartmentName(d.getName());
                    if (d.getManagerId() != null) {
                        staffRepository.findById(d.getManagerId()).ifPresent(manager -> staff.setDepartmentManagerName(manager.getUsername()));
                    } else {
                        staff.setDepartmentManagerName("无");
                    }
                    staff.setIsManager(staffDepartment.get().getIsManager());
                });
            } else {
                staff.setDepartmentId(null);
                staff.setDepartmentName(null);
                staff.setDepartmentManagerName(null);
                staff.setIsManager(false);
            }
            return ResponseEntity.ok(staff);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/staff/joinDepartment/{deptId}")
    public ResponseEntity<Staff> joinDepartment(@RequestParam Long staffId, @PathVariable Integer deptId) {
        Optional<Staff> staffOptional = staffRepository.findById(staffId);
        Optional<Department> departmentOptional = departmentRepository.findById(deptId);

        if (staffOptional.isEmpty() || departmentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 检查员工是否已经加入其他部门
        if (staffDepartmentRepository.findByStaffId(staffId).isPresent()) {
            return ResponseEntity.badRequest().build(); // 员工已在部门中
        }

        StaffDepartment staffDepartment = new StaffDepartment();
        staffDepartment.setStaffId(staffId);
        staffDepartment.setDeptId(deptId);
        staffDepartment.setIsManager(false); // 默认不是经理
        staffDepartmentRepository.save(staffDepartment);

        // 重新获取并返回更新后的员工信息
        return getCurrentStaff(staffId);
    }
}