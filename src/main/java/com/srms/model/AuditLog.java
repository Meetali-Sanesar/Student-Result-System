package com.srms.model;

import java.sql.Timestamp;

/**
 * Represents an audit log entry tracking admin activities.
 */
public class AuditLog {

    private int logId;
    private int adminId;
    private String action;
    private String details;
    private Timestamp actionTime;

    // Transient field for display
    private String adminUsername;

    public AuditLog() {}

    public AuditLog(int logId, int adminId, String action, String details, Timestamp actionTime) {
        this.logId = logId;
        this.adminId = adminId;
        this.action = action;
        this.details = details;
        this.actionTime = actionTime;
    }

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Timestamp getActionTime() { return actionTime; }
    public void setActionTime(Timestamp actionTime) { this.actionTime = actionTime; }

    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }

    @Override
    public String toString() {
        return "AuditLog{logId=" + logId + ", action='" + action + "', time=" + actionTime + "}";
    }
}
