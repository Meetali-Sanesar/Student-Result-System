package com.srms.controller;

import com.srms.model.AuditLog;
import com.srms.service.AuditLogService;
import com.srms.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for audit logs.
 * GET /api/audit-logs - Get all audit logs (admin only)
 */
public class AuditLogController extends HttpServlet {

    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String limitStr = request.getParameter("limit");
            List<AuditLog> logs;
            if (limitStr != null) {
                int limit = Integer.parseInt(limitStr);
                logs = auditLogService.getRecentLogs(limit);
            } else {
                logs = auditLogService.getAllLogs();
            }
            ResponseUtil.sendSuccess(response, logs);
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid limit value");
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }
}
