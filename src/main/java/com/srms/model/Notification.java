package com.srms.model;

import java.sql.Timestamp;

/**
 * Represents a notification for a student.
 */
public class Notification {

    private int notificationId;
    private int studentId;
    private String message;
    private boolean read;
    private Timestamp createdAt;

    public Notification() {}

    public Notification(int notificationId, int studentId, String message,
                        boolean read, Timestamp createdAt) {
        this.notificationId = notificationId;
        this.studentId = studentId;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Notification{id=" + notificationId + ", studentId=" + studentId +
               ", read=" + read + "}";
    }
}
