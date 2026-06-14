package com.srms.controller;

import com.srms.exception.AppException;
import com.srms.model.User;
import com.srms.service.AuthService;
import com.srms.util.JsonUtil;
import com.srms.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for authentication operations.
 * POST /api/auth/login  - Authenticate user
 * POST /api/auth/logout - Logout user
 * GET  /api/auth/me     - Get current user
 */
public class AuthController extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);

        try {
            if (segments.length == 1 && "login".equals(segments[0])) {
                handleLogin(request, response);
            } else if (segments.length == 1 && "register".equals(segments[0])) {
                handleRegister(request, response);
            } else if (segments.length == 1 && "logout".equals(segments[0])) {
                handleLogout(request, response);
            } else {
                ResponseUtil.sendNotFound(response, "Endpoint not found");
            }
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);

        try {
            if (segments.length == 1 && "me".equals(segments[0])) {
                User user = authService.getCurrentUser(request);
                if (user == null) {
                    ResponseUtil.sendUnauthorized(response, "Not authenticated");
                } else {
                    ResponseUtil.sendSuccess(response, user);
                }
            } else {
                ResponseUtil.sendNotFound(response, "Endpoint not found");
            }
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = ResponseUtil.readRequestBody(request);
        Map<String, Object> credentials = JsonUtil.fromJson(body, Map.class);

        String username = (String) credentials.get("username");
        String password = (String) credentials.get("password");
        
        boolean rememberMe = false;
        if (credentials.containsKey("rememberMe")) {
            Object rmObj = credentials.get("rememberMe");
            if (rmObj instanceof Boolean) {
                rememberMe = (Boolean) rmObj;
            } else if (rmObj instanceof String) {
                rememberMe = Boolean.parseBoolean((String) rmObj);
            }
        }

        User user = authService.login(request, username, password, rememberMe);

        if (rememberMe && user.getRememberToken() != null) {
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("remember_me", user.getRememberToken());
            cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
            cookie.setPath(request.getContextPath() + "/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("role", user.getRole());
        data.put("studentId", user.getStudentId());

        ResponseUtil.sendSuccess(response, data, "Login successful");
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = ResponseUtil.readRequestBody(request);
        Map<String, String> regData = JsonUtil.fromJson(body, Map.class);

        String name = regData.get("name");
        String email = regData.get("email");
        String phone = regData.get("phone");
        String department = regData.get("department");
        String username = regData.get("username");
        String password = regData.get("password");

        User user = authService.registerStudent(name, email, phone, department, username, password);

        ResponseUtil.sendSuccess(response, user, "Registration successful");
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.logout(request);

        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("remember_me", "");
        cookie.setMaxAge(0);
        cookie.setPath(request.getContextPath() + "/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        ResponseUtil.sendSuccess(response, null, "Logout successful");
    }
}
