package com.srms.controller;

import com.srms.exception.AppException;
import com.srms.model.Result;
import com.srms.security.SessionManager;
import com.srms.service.AuditLogService;
import com.srms.service.ResultService;
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
 * REST Controller for result management.
 */
public class ResultController extends HttpServlet {

    private final ResultService resultService = new ResultService();
    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);
        try {
            if (segments.length == 1 && "rankings".equals(segments[0])) {
                // GET /api/results/rankings
                List<Result> results = resultService.getAllResults();
                ResponseUtil.sendSuccess(response, results);

            } else if (segments.length == 1 && "top-students".equals(segments[0])) {
                // GET /api/results/top-students?limit=10
                String limitStr = request.getParameter("limit");
                int limit = (limitStr != null) ? Integer.parseInt(limitStr) : 10;
                List<Result> topStudents = resultService.getTopStudents(limit);
                ResponseUtil.sendSuccess(response, topStudents);

            } else if (segments.length == 1) {
                // GET /api/results/{studentId}
                int studentId = Integer.parseInt(segments[0]);
                Result result = resultService.getResultByStudentId(studentId);
                ResponseUtil.sendSuccess(response, result);

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
        String[] segments = ResponseUtil.getPathSegments(request);
        try {
            if (segments.length == 2 && "generate".equals(segments[0])) {
                // POST /api/results/generate/{studentId}
                int studentId = Integer.parseInt(segments[1]);
                Result result = resultService.generateResult(studentId);

                Integer adminId = SessionManager.getUserId(request);
                if (adminId != null) {
                    auditLogService.logAction(adminId, "RESULT_GENERATED",
                            "Generated result for student ID: " + studentId);
                }

                ResponseUtil.sendCreated(response, result, "Result generated successfully");

            } else if (segments.length == 1 && "generate-all".equals(segments[0])) {
                // POST /api/results/generate-all
                int count = resultService.generateAllResults();

                Integer adminId = SessionManager.getUserId(request);
                if (adminId != null) {
                    auditLogService.logAction(adminId, "RESULT_GENERATED",
                            "Generated results for " + count + " students");
                }

                Map<String, Object> data = new HashMap<>();
                data.put("count", count);
                ResponseUtil.sendCreated(response, data, "Results generated for " + count + " students");

            } else if (segments.length == 1 && "publish".equals(segments[0])) {
                // POST /api/results/publish
                resultService.publishResults();

                Integer adminId = SessionManager.getUserId(request);
                if (adminId != null) {
                    auditLogService.logAction(adminId, "RESULT_PUBLISHED",
                            "Published results for all students");
                }

                ResponseUtil.sendSuccess(response, null, "Results published successfully");

            } else {
                ResponseUtil.sendNotFound(response, "Endpoint not found");
            }
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid student ID");
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }
}
