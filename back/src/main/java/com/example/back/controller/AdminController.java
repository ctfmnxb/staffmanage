package com.example.back.controller;

import com.example.back.entity.Admin;
import com.example.back.repository.AdminRepository;
import com.example.back.dto.AttendanceStatusDTO;
import com.example.back.controller.AttendanceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AttendanceController attendanceController;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> user) {
        Map<String, Object> response = new HashMap<>();
        String username = user.get("username");
        String password = user.get("password");

        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            if (adminRepository.findByUsername(username) != null) {
                response.put("success", false);
                response.put("message", "用户名已存在");
            } else {
                Admin admin = new Admin(username, password);
                adminRepository.save(admin);
                response.put("success", true);
                response.put("message", "注册成功");
            }
        } else {
            response.put("success", false);
            response.put("message", "用户名和密码不能为空");
        }
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> user) {
        System.out.println("Received user map: " + user);
        Map<String, Object> response = new HashMap<>();
        String username = user.get("username");
        String password = user.get("password");

        if (username != null && password != null) {
            Admin existingAdmin = adminRepository.findByUsername(username);
            if (existingAdmin != null && existingAdmin.getPassword().equals(password)) {
                response.put("success", true);
                response.put("message", "登录成功");
                response.put("username", username);
            } else {
                response.put("success", false);
                response.put("message", "用户名或密码错误");
            }
        } else {
            response.put("success", false);
            response.put("message", "用户名和密码不能为空");
        }
        return response;
    }

    @GetMapping("/verify/{username}")
    public Map<String, Object> verifyAdmin(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null) {
            response.put("success", true);
            response.put("message", "验证成功");
        } else {
            response.put("success", false);
            response.put("message", "非管理员用户");
        }
        return response;
    }

    @GetMapping("/attendance-status/{date}")
    public List<AttendanceStatusDTO> getAllEmployeesAttendanceStatus(@PathVariable String date) {
        return attendanceController.getAllEmployeesAttendanceStatusForDate(date);
    }
}