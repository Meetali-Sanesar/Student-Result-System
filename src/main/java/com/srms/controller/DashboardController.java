package com.srms.controller;

import com.srms.model.Result;
import com.srms.service.ResultService;
import com.srms.service.StudentService;
import com.srms.service.SubjectService;
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
 * REST Controller for admin dashboard analytics.
 * GET /api/dashboard/stats - Get comprehensive dashboard statistics
 */
public class DashboardController extends HttpServlet {

    private final StudentService studentService = new StudentService();
    private final SubjectService subjectService = new SubjectService();
    private final ResultService resultService = new ResultService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] segments = ResponseUtil.getPathSegments(request);

        try {
            if (segments.length == 1 && "stats".equals(segments[0])) {
                Map<String, Object> stats = new HashMap<>();

                // Basic counts
                stats.put("totalStudents", studentService.getStudentCount());
                stats.put("totalSubjects", subjectService.getSubjectCount());
                stats.put("totalResults", resultService.getResultCount());

                // Pass/fail
                int passed = resultService.getPassedCount();
                int failed = resultService.getFailedCount();
                int total = passed + failed;
                stats.put("passedCount", passed);
                stats.put("failedCount", failed);
                stats.put("passPercentage", total > 0 ? Math.round((double) passed / total * 100 * 100.0) / 100.0 : 0);

                // Grade distribution
                Map<String, Integer> gradeDistribution = new HashMap<>();
                gradeDistribution.put("A+", resultService.getCountByGrade("A+"));
                gradeDistribution.put("A", resultService.getCountByGrade("A"));
                gradeDistribution.put("B", resultService.getCountByGrade("B"));
                gradeDistribution.put("C", resultService.getCountByGrade("C"));
                gradeDistribution.put("F", resultService.getCountByGrade("F"));
                stats.put("gradeDistribution", gradeDistribution);

                // Top student
                List<Result> topStudents = resultService.getTopStudents(1);
                if (!topStudents.isEmpty()) {
                    Result top = topStudents.get(0);
                    Map<String, Object> topStudent = new HashMap<>();
                    topStudent.put("studentName", top.getStudentName());
                    topStudent.put("percentage", top.getPercentage());
                    topStudent.put("grade", top.getGrade());
                    topStudent.put("department", top.getDepartment());
                    stats.put("topStudent", topStudent);
                }

                // Department-wise statistics
                List<String> departments = studentService.getDepartments();
                Map<String, Object> deptStats = new HashMap<>();
                for (String dept : departments) {
                    Map<String, Object> deptInfo = new HashMap<>();
                    List<com.srms.model.Student> deptStudents = studentService.filterByDepartment(dept);
                    deptInfo.put("studentCount", deptStudents.size());
                    deptStats.put(dept, deptInfo);
                }
                stats.put("departmentStats", deptStats);
                stats.put("departments", departments);

                ResponseUtil.sendSuccess(response, stats);
            } else {
                ResponseUtil.sendNotFound(response, "Endpoint not found");
            }
        } catch (Exception e) {
            ResponseUtil.sendServerError(response, "Internal server error: " + e.getMessage());
        }
    }
}
