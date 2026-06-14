package com.srms.controller;

import com.srms.exception.AppException;
import com.srms.model.Student;
import com.srms.security.SessionManager;
import com.srms.service.AuditLogService;
import com.srms.service.StudentService;
import com.srms.util.JsonUtil;
import com.srms.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for student management.
 * GET    /api/students              - List all students
 * GET    /api/students/{id}         - Get student by ID
 * GET    /api/students/search?q=    - Search students
 * GET    /api/students/filter?dept= - Filter by department
 * GET    /api/students/departments  - Get distinct departments
 * POST   /api/students              - Create student
 * PUT    /api/students/{id}         - Update student
 * DELETE /api/students/{id}         - Delete student
 */
public class StudentController extends HttpServlet {

    private final StudentService studentService = new StudentService();
    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);

        try {
            if (segments.length == 0) {
                // GET /api/students
                List<Student> students = studentService.getAllStudents();
                ResponseUtil.sendSuccess(response, students);

            } else if (segments.length == 1 && "search".equals(segments[0])) {
                // GET /api/students/search?q=...
                String query = request.getParameter("q");
                List<Student> students = studentService.searchStudents(query);
                ResponseUtil.sendSuccess(response, students);

            } else if (segments.length == 1 && "filter".equals(segments[0])) {
                // GET /api/students/filter?dept=...
                String dept = request.getParameter("dept");
                List<Student> students = studentService.filterByDepartment(dept);
                ResponseUtil.sendSuccess(response, students);

            } else if (segments.length == 1 && "departments".equals(segments[0])) {
                // GET /api/students/departments
                List<String> departments = studentService.getDepartments();
                ResponseUtil.sendSuccess(response, departments);

            } else if (segments.length == 1) {
                // GET /api/students/{id}
                int id = Integer.parseInt(segments[0]);
                Student student = studentService.getStudent(id);
                ResponseUtil.sendSuccess(response, student);

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String body = ResponseUtil.readRequestBody(request);
            Student student = JsonUtil.fromJson(body, Student.class);
            Student created = studentService.addStudent(student);

            // Audit log
            Integer adminId = SessionManager.getUserId(request);
            if (adminId != null) {
                auditLogService.logAction(adminId, "STUDENT_ADDED",
                        "Added student: " + created.getName() + " (ID: " + created.getStudentId() + ")");
            }

            ResponseUtil.sendCreated(response, created, "Student added successfully");
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
                ResponseUtil.sendBadRequest(response, "Student ID is required");
                return;
            }

            int id = Integer.parseInt(segments[0]);
            String body = ResponseUtil.readRequestBody(request);
            Student student = JsonUtil.fromJson(body, Student.class);
            Student updated = studentService.updateStudent(id, student);

            // Audit log
            Integer adminId = SessionManager.getUserId(request);
            if (adminId != null) {
                auditLogService.logAction(adminId, "STUDENT_UPDATED",
                        "Updated student: " + updated.getName() + " (ID: " + id + ")");
            }

            ResponseUtil.sendSuccess(response, updated, "Student updated successfully");
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid student ID");
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
                ResponseUtil.sendBadRequest(response, "Student ID is required");
                return;
            }

            int id = Integer.parseInt(segments[0]);
            Student student = studentService.getStudent(id);
            studentService.deleteStudent(id);

            // Audit log
            Integer adminId = SessionManager.getUserId(request);
            if (adminId != null) {
                auditLogService.logAction(adminId, "STUDENT_DELETED",
                        "Deleted student: " + student.getName() + " (ID: " + id + ")");
            }

            ResponseUtil.sendSuccess(response, null, "Student deleted successfully");
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (NumberFormatException e) {
            ResponseUtil.sendBadRequest(response, "Invalid student ID");
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }
}
