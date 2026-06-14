package com.srms.controller;

import com.srms.exception.AppException;
import com.srms.model.Notification;
import com.srms.security.SessionManager;
import com.srms.service.NotificationService;
import com.srms.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for notifications.
 * GET /api/notifications       - Get student notifications
 * PUT /api/notifications/{id}/read - Mark notification as read
 */
public class NotificationController extends HttpServlet {

    private final NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Integer studentId = SessionManager.getStudentId(request);
            if (studentId == null) {
                ResponseUtil.sendBadRequest(response, "Student ID not found in session");
                return;
            }

            List<Notification> notifications = notificationService.getStudentNotifications(studentId);
            int unreadCount = notificationService.getUnreadCount(studentId);

            Map<String, Object> data = new HashMap<>();
            data.put("notifications", notifications);
            data.put("unreadCount", unreadCount);

            ResponseUtil.sendSuccess(response, data);
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);
        try {
            if (segments.length == 2 && "read".equals(segments[1])) {
                int notificationId = Integer.parseInt(segments[0]);
                notificationService.markAsRead(notificationId);
                ResponseUtil.sendSuccess(response, null, "Notification marked as read");
            } else {
                ResponseUtil.sendNotFound(response, "Endpoint not found");
            }
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid notification ID");
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }
}
