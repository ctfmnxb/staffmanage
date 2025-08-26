package com.example.back.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Admin {
    @Id
    private String username;
    private String password;


    // 无参构造函数
    public Admin() {
    }

    // 有参构造函数
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;

    }



    // Getters 和 Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}