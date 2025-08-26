package com.example.back.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "daily_checkin")
public class Attendance implements Serializable {

    @EmbeddedId
    private AttendanceId id;

    public Attendance() {
    }

    public Attendance(AttendanceId id) {
        this.id = id;
    }

    public AttendanceId getId() {
        return id;
    }

    public void setId(AttendanceId id) {
        this.id = id;
    }
}