package com.srms.controller;

import com.srms.dao.MarkDAO;
import com.srms.dao.ResultDAO;
import com.srms.dao.StudentDAO;
import com.srms.exception.AppException;
import com.srms.model.Mark;
import com.srms.model.Result;
import com.srms.model.Student;
import com.srms.security.SessionManager;
import com.srms.service.ExcelService;
import com.srms.service.PdfService;
import com.srms.service.StudentService;
import com.srms.util.FileUtil;
import com.srms.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * REST Controller for file operations.
 * POST /api/files/upload-image          - Upload profile image
 * GET  /api/files/result-pdf/{studentId} - Download result PDF
 * GET  /api/files/results-excel         - Download results Excel
 */
public class FileController extends HttpServlet {

    private final PdfService pdfService = new PdfService();
    private final ExcelService excelService = new ExcelService();
    private final StudentService studentService = new StudentService();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ResultDAO resultDAO = new ResultDAO();
    private final MarkDAO markDAO = new MarkDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);

        try {
            if (segments.length == 2 && "result-pdf".equals(segments[0])) {
                // GET /api/files/result-pdf/{studentId}
                int studentId = Integer.parseInt(segments[1]);
                generateResultPdf(studentId, response);

            } else if (segments.length == 1 && "results-excel".equals(segments[0])) {
                // GET /api/files/results-excel
                generateResultsExcel(response);

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
        String[] segments = ResponseUtil.getPathSegments(request);

        try {
            if (segments.length == 1 && "upload-image".equals(segments[0])) {
                handleImageUpload(request, response);
            } else {
                ResponseUtil.sendNotFound(response, "Endpoint not found");
            }
        } catch (AppException e) {
            ResponseUtil.sendError(response, e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }

    private void handleImageUpload(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Integer studentId = null;

        // Get student ID from parameter or session
        String studentIdParam = request.getParameter("studentId");
        if (studentIdParam != null) {
            studentId = Integer.parseInt(studentIdParam);
        } else {
            studentId = SessionManager.getStudentId(request);
        }

        if (studentId == null) {
            ResponseUtil.sendBadRequest(response, "Student ID is required");
            return;
        }

        Part filePart = request.getPart("image");
        if (filePart == null || filePart.getSize() == 0) {
            ResponseUtil.sendBadRequest(response, "No image file provided");
            return;
        }

        // Validate image
        String contentType = filePart.getContentType();
        long fileSize = filePart.getSize();
        String fileName = filePart.getSubmittedFileName();
        String error = FileUtil.validateImage(contentType, fileSize, fileName);
        if (error != null) {
            ResponseUtil.sendBadRequest(response, error);
            return;
        }

        // Save file
        String uploadDir = FileUtil.getUploadDir(getServletContext());
        try (InputStream inputStream = filePart.getInputStream()) {
            String savedName = FileUtil.saveFile(inputStream, uploadDir, fileName);
            studentService.updateProfileImage(studentId, savedName);
            ResponseUtil.sendSuccess(response, savedName, "Profile image uploaded successfully");
        }
    }

    private void generateResultPdf(int studentId, HttpServletResponse response) throws IOException {
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            ResponseUtil.sendNotFound(response, "Student not found");
            return;
        }

        Result result = resultDAO.findByStudentId(studentId);
        if (result == null) {
            ResponseUtil.sendNotFound(response, "No result found for this student");
            return;
        }

        List<Mark> marks = markDAO.findByStudentId(studentId);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=result_" + student.getName().replaceAll("\\s+", "_") + ".pdf");

        pdfService.generateResultPdf(student, result, marks, response.getOutputStream());
    }

    private void generateResultsExcel(HttpServletResponse response) throws IOException {
        List<Result> results = resultDAO.findAll();
        if (results.isEmpty()) {
            ResponseUtil.sendNotFound(response, "No results to export");
            return;
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=student_results.xlsx");

        excelService.exportResults(results, response.getOutputStream());
    }
}
