package com.srms.service;

import com.srms.dao.AuditLogDAO;
import com.srms.model.AuditLog;

import java.util.List;

/**
 * Service for audit log operations.
 */
public class AuditLogService {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    /**
     * Log an admin action.
     */
    public void logAction(int adminId, String action, String details) {
        AuditLog log = new AuditLog();
        log.setAdminId(adminId);
        log.setAction(action);
        log.setDetails(details);
        auditLogDAO.create(log);
    }

    /**
     * Get all audit logs.
     */
    public List<AuditLog> getAllLogs() {
        return auditLogDAO.findAll();
    }

    /**
     * Get recent audit logs.
     */
    public List<AuditLog> getRecentLogs(int limit) {
        return auditLogDAO.findRecentLogs(limit);
    }
}
