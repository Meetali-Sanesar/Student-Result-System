package com.srms.controller;

import com.srms.exception.AppException;
import com.srms.model.Mark;
import com.srms.security.SessionManager;
import com.srms.service.AuditLogService;
import com.srms.service.MarkService;
import com.srms.util.JsonUtil;
import com.srms.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for marks management.
 */
public class MarkController extends HttpServlet {

    private final MarkService markService = new MarkService();
    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);
        try {
            if (segments.length == 0) {
                // GET /api/marks - all marks
                List<Mark> marks = markService.getAllMarks();
                ResponseUtil.sendSuccess(response, marks);
            } else if (segments.length == 2 && "student".equals(segments[0])) {
                // GET /api/marks/student/{id}
                int studentId = Integer.parseInt(segments[1]);
                List<Mark> marks = markService.getMarksByStudentId(studentId);
                ResponseUtil.sendSuccess(response, marks);
            } else if (segments.length == 1) {
                // GET /api/marks/{id}
                int markId = Integer.parseInt(segments[0]);
                Mark mark = markService.getMark(markId);
                ResponseUtil.sendSuccess(response, mark);
            } else {
                ResponseUtil.sendNotFound(response, "Endpoint not found");
            }
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid ID format");
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String body = ResponseUtil.readRequestBody(request);
            Mark mark = JsonUtil.fromJson(body, Mark.class);
            Mark created = markService.addMark(mark);

            Integer adminId = SessionManager.getUserId(request);
            if (adminId != null) {
                auditLogService.logAction(adminId, "MARKS_ENTERED",
                        "Entered marks for student ID: " + mark.getStudentId() +
                        ", subject ID: " + mark.getSubjectId() + ", marks: " + mark.getMarks());
            }

            ResponseUtil.sendCreated(response, created, "Marks added successfully");
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);
        try {
            if (segments.length != 1) {
                ResponseUtil.sendBadRequest(response, "Mark ID is required");
                return;
            }
            int markId = Integer.parseInt(segments[0]);
            String body = ResponseUtil.readRequestBody(request);
            Map<String, Object> data = JsonUtil.fromJson(body, Map.class);
            double newMarks = ((Number) data.get("marks")).doubleValue();

            Mark updated = markService.updateMark(markId, newMarks);

            Integer adminId = SessionManager.getUserId(request);
            if (adminId != null) {
                auditLogService.logAction(adminId, "MARKS_UPDATED",
                        "Updated mark ID: " + markId + ", new marks: " + newMarks);
            }

            ResponseUtil.sendSuccess(response, updated, "Marks updated successfully");
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid ID or marks format");
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);
        try {
            if (segments.length != 1) {
                ResponseUtil.sendBadRequest(response, "Mark ID is required");
                return;
            }
            int markId = Integer.parseInt(segments[0]);
            markService.deleteMark(markId);

            Integer adminId = SessionManager.getUserId(request);
            if (adminId != null) {
                auditLogService.logAction(adminId, "MARKS_DELETED",
                        "Deleted mark ID: " + markId);
            }

            ResponseUtil.sendSuccess(response, null, "Marks deleted successfully");
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid mark ID");
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }
}
