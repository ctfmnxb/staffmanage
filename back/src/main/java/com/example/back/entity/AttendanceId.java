package com.example.back.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AttendanceId implements Serializable {

    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "check_date")
    private String checkDate;

    public AttendanceId() {
    }

    public AttendanceId(Long staffId, String checkDate) {
        this.staffId = staffId;
        this.checkDate = checkDate;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceId that = (AttendanceId) o;
        return Objects.equals(staffId, that.staffId) &&
               Objects.equals(checkDate, that.checkDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffId, checkDate);
    }
}