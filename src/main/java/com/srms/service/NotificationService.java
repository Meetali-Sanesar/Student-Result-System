package com.srms.service;

import com.srms.dao.NotificationDAO;
import com.srms.model.Notification;

import java.util.List;

/**
 * Service for notification operations.
 */
public class NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    /**
     * Create a notification for a student.
     */
    public void createNotification(int studentId, String message) {
        Notification notification = new Notification();
        notification.setStudentId(studentId);
        notification.setMessage(message);
        notificationDAO.create(notification);
    }

    /**
     * Create a notification for all students.
     */
    public void notifyAllStudents(String message) {
        notificationDAO.createForAllStudents(message);
    }

    /**
     * Get notifications for a student.
     */
    public List<Notification> getStudentNotifications(int studentId) {
        return notificationDAO.findByStudentId(studentId);
    }

    /**
     * Mark a notification as read.
     */
    public void markAsRead(int notificationId) {
        notificationDAO.markAsRead(notificationId);
    }

    /**
     * Get unread notification count for a student.
     */
    public int getUnreadCount(int studentId) {
        return notificationDAO.getUnreadCount(studentId);
    }
}
