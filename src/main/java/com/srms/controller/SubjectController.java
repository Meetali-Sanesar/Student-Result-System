package com.srms.controller;

import com.srms.exception.AppException;
import com.srms.model.Subject;
import com.srms.security.SessionManager;
import com.srms.service.AuditLogService;
import com.srms.service.SubjectService;
import com.srms.util.JsonUtil;
import com.srms.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for subject management.
 */
public class SubjectController extends HttpServlet {

    private final SubjectService subjectService = new SubjectService();
    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);
        try {
            if (segments.length == 0) {
                List<Subject> subjects = subjectService.getAllSubjects();
                ResponseUtil.sendSuccess(response, subjects);
            } else if (segments.length == 1) {
                int id = Integer.parseInt(segments[0]);
                Subject subject = subjectService.getSubject(id);
                ResponseUtil.sendSuccess(response, subject);
            } else {
                ResponseUtil.sendNotFound(response, "Endpoint not found");
            }
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid subject ID");
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String body = ResponseUtil.readRequestBody(request);
            Subject subject = JsonUtil.fromJson(body, Subject.class);
            Subject created = subjectService.addSubject(subject);

            Integer adminId = SessionManager.getUserId(request);
            if (adminId != null) {
                auditLogService.logAction(adminId, "SUBJECT_ADDED",
                        "Added subject: " + created.getSubjectName() + " (" + created.getSubjectCode() + ")");
            }

            ResponseUtil.sendCreated(response, created, "Subject added successfully");
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
                ResponseUtil.sendBadRequest(response, "Subject ID is required");
                return;
            }
            int id = Integer.parseInt(segments[0]);
            String body = ResponseUtil.readRequestBody(request);
            Subject subject = JsonUtil.fromJson(body, Subject.class);
            Subject updated = subjectService.updateSubject(id, subject);

            Integer adminId = SessionManager.getUserId(request);
            if (adminId != null) {
                auditLogService.logAction(adminId, "SUBJECT_UPDATED",
                        "Updated subject: " + updated.getSubjectName() + " (ID: " + id + ")");
            }

            ResponseUtil.sendSuccess(response, updated, "Subject updated successfully");
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid subject ID");
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
                ResponseUtil.sendBadRequest(response, "Subject ID is required");
                return;
            }
            int id = Integer.parseInt(segments[0]);
            Subject subject = subjectService.getSubject(id);
            subjectService.deleteSubject(id);

            Integer adminId = SessionManager.getUserId(request);
            if (adminId != null) {
                auditLogService.logAction(adminId, "SUBJECT_DELETED",
                        "Deleted subject: " + subject.getSubjectName() + " (ID: " + id + ")");
            }

            ResponseUtil.sendSuccess(response, null, "Subject deleted successfully");
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid subject ID");
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }
}
