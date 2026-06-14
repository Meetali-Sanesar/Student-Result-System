package com.srms.model;

import java.sql.Timestamp;

/**
 * Represents a user in the system (Admin or Student).
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String role;
    private Integer studentId;
    private Timestamp createdAt;
    private String rememberToken;

    public User() {}

    public User(int userId, String username, String password, String role, Integer studentId, Timestamp createdAt, String rememberToken) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.studentId = studentId;
        this.createdAt = createdAt;
        this.rememberToken = rememberToken;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getRememberToken() { return rememberToken; }
    public void setRememberToken(String rememberToken) { this.rememberToken = rememberToken; }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username='" + username + "', role='" + role + "'}";
    }
}
