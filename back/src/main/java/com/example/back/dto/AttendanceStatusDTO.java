package com.example.back.dto;

import java.util.Date;

public class AttendanceStatusDTO {
    private Long staffId;
    private String staffName;
    private Date checkDate;
    private Integer status;
    private String leaveReason;

    // Constructors
    public AttendanceStatusDTO() {
    }

    public AttendanceStatusDTO(Long staffId, String staffName, Date checkDate, Integer status, String leaveReason) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.checkDate = checkDate;
        this.status = status;
        this.leaveReason = leaveReason;
    }

    // Getters and Setters
    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }
}