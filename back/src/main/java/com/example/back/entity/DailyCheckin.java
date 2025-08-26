package com.example.back.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "daily_checkin")
@IdClass(DailyCheckin.DailyCheckinId.class)
public class DailyCheckin {

    @Id
    @Column(name = "staff_id")
    private Long staffId;

    @Id
    @Column(name = "check_date")
    private LocalDate checkDate;

    // Getters and Setters
    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
    }

    // Composite Primary Key Class
    public static class DailyCheckinId implements Serializable {
        private Long staffId;
        private LocalDate checkDate;

        public DailyCheckinId() {}

        public DailyCheckinId(Long staffId, LocalDate checkDate) {
            this.staffId = staffId;
            this.checkDate = checkDate;
        }

        // equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DailyCheckinId that = (DailyCheckinId) o;
            return staffId.equals(that.staffId) && checkDate.equals(that.checkDate);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(staffId, checkDate);
        }
    }
}