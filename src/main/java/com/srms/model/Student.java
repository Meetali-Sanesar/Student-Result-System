package com.srms.model;

import java.sql.Timestamp;

/**
 * Represents a student record.
 */
public class Student {

    private int studentId;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String profileImage;
    private Timestamp createdAt;

    public Student() {}

    public Student(int studentId, String name, String email, String phone,
                   String department, String profileImage, Timestamp createdAt) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Student{studentId=" + studentId + ", name='" + name + "', department='" + department + "'}";
    }
}
