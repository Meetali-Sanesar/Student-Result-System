package com.srms.security;

import com.srms.model.User;
import com.srms.dao.UserDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

/**
 * Manages user sessions for authentication and authorization.
 */
public class SessionManager {

    public static final String USER_SESSION_KEY = "loggedInUser";
    public static final String USER_ID_KEY = "userId";
    public static final String USER_ROLE_KEY = "userRole";
    public static final String STUDENT_ID_KEY = "studentId";

    private SessionManager() {}

    /**
     * Create a session for the authenticated user.
     */
    public static void createSession(HttpServletRequest request, User user) {
        // Invalidate any existing session to prevent session fixation
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(USER_SESSION_KEY, user);
        session.setAttribute(USER_ID_KEY, user.getUserId());
        session.setAttribute(USER_ROLE_KEY, user.getRole());

        if (user.getStudentId() != null) {
            session.setAttribute(STUDENT_ID_KEY, user.getStudentId());
        }

        // Session timeout: 30 minutes
        session.setMaxInactiveInterval(30 * 60);
    }

    /**
     * Get the currently logged-in user from the session.
     */
    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute(USER_SESSION_KEY);
            if (user != null) {
                return user;
            }
        }
        return attemptAutoLogin(request);
    }

    /**
     * Get the user ID from the session.
     */
    public static Integer getUserId(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return user != null ? user.getUserId() : null;
    }

    /**
     * Get the student ID from the session (null for admins).
     */
    public static Integer getStudentId(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return user != null ? user.getStudentId() : null;
    }

    /**
     * Get the user's role from the session.
     */
    public static String getUserRole(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return user != null ? user.getRole() : null;
    }

    /**
     * Helper to auto-login from the remember_me cookie.
     */
    private static User attemptAutoLogin(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("remember_me".equals(cookie.getName())) {
                String token = cookie.getValue();
                if (token != null && !token.trim().isEmpty()) {
                    UserDAO userDAO = new UserDAO();
                    User user = userDAO.findByRememberToken(token);
                    if (user != null) {
                        HttpSession session = request.getSession(true);
                        session.setAttribute(USER_SESSION_KEY, user);
                        session.setAttribute(USER_ID_KEY, user.getUserId());
                        session.setAttribute(USER_ROLE_KEY, user.getRole());
                        if (user.getStudentId() != null) {
                            session.setAttribute(STUDENT_ID_KEY, user.getStudentId());
                        }
                        session.setMaxInactiveInterval(30 * 60);
                        return user;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check if the current user is authenticated.
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        return getLoggedInUser(request) != null;
    }

    /**
     * Check if the current user is an admin.
     */
    public static boolean isAdmin(HttpServletRequest request) {
        String role = getUserRole(request);
        return "ADMIN".equals(role);
    }

    /**
     * Check if the current user is a student.
     */
    public static boolean isStudent(HttpServletRequest request) {
        String role = getUserRole(request);
        return "STUDENT".equals(role);
    }

    /**
     * Invalidate the current session (logout).
     */
    public static void destroySession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
