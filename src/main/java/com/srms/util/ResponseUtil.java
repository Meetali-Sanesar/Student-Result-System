package com.srms.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for sending standardized JSON API responses.
 * All API responses follow the format:
 * { "success": true/false, "data": ..., "message": "..." }
 */
public class ResponseUtil {

    private ResponseUtil() {}

    /**
     * Send a success response with data.
     */
    public static void sendSuccess(HttpServletResponse response, Object data) throws IOException {
        sendResponse(response, HttpServletResponse.SC_OK, true, data, null);
    }

    /**
     * Send a success response with data and message.
     */
    public static void sendSuccess(HttpServletResponse response, Object data, String message) throws IOException {
        sendResponse(response, HttpServletResponse.SC_OK, true, data, message);
    }

    /**
     * Send a created (201) response.
     */
    public static void sendCreated(HttpServletResponse response, Object data, String message) throws IOException {
        sendResponse(response, HttpServletResponse.SC_CREATED, true, data, message);
    }

    /**
     * Send an error response with status code and message.
     */
    public static void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        sendResponse(response, statusCode, false, null, message);
    }

    /**
     * Send a 400 Bad Request error.
     */
    public static void sendBadRequest(HttpServletResponse response, String message) throws IOException {
        sendError(response, HttpServletResponse.SC_BAD_REQUEST, message);
    }

    /**
     * Send a 401 Unauthorized error.
     */
    public static void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        sendError(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    /**
     * Send a 403 Forbidden error.
     */
    public static void sendForbidden(HttpServletResponse response, String message) throws IOException {
        sendError(response, HttpServletResponse.SC_FORBIDDEN, message);
    }

    /**
     * Send a 404 Not Found error.
     */
    public static void sendNotFound(HttpServletResponse response, String message) throws IOException {
        sendError(response, HttpServletResponse.SC_NOT_FOUND, message);
    }

    /**
     * Send a 500 Internal Server Error.
     */
    public static void sendServerError(HttpServletResponse response, String message) throws IOException {
        sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    /**
     * Core method to send a JSON response.
     */
    private static void sendResponse(HttpServletResponse response, int statusCode,
                                     boolean success, Object data, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", success);
        if (data != null) {
            responseBody.put("data", data);
        }
        if (message != null) {
            responseBody.put("message", message);
        }

        PrintWriter writer = response.getWriter();
        writer.print(JsonUtil.toJson(responseBody));
        writer.flush();
    }

    /**
     * Read the request body as a string.
     */
    public static String readRequestBody(jakarta.servlet.http.HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (java.io.BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * Extract path info segments from request.
     * e.g., "/students/5" -> ["students", "5"]
     */
    public static String[] getPathSegments(jakarta.servlet.http.HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return new String[0];
        }
        // Remove leading slash and split
        return pathInfo.substring(1).split("/");
    }
}
