package com.srms.service;

import com.srms.dao.MarkDAO;
import com.srms.dao.NotificationDAO;
import com.srms.dao.ResultDAO;
import com.srms.dao.StudentDAO;
import com.srms.exception.ResourceNotFoundException;
import com.srms.exception.ValidationException;
import com.srms.model.Mark;
import com.srms.model.Notification;
import com.srms.model.Result;
import com.srms.model.Student;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service for result generation and management.
 */
public class ResultService {

    private static final Logger LOGGER = Logger.getLogger(ResultService.class.getName());
    private final ResultDAO resultDAO = new ResultDAO();
    private final MarkDAO markDAO = new MarkDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    /**
     * Generate result for a specific student.
     * Calculates total, percentage, grade.
     */
    public Result generateResult(int studentId) {
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }

        List<Mark> marks = markDAO.findByStudentId(studentId);
        if (marks.isEmpty()) {
            throw new ValidationException("No marks found for student ID: " + studentId + ". Enter marks first.");
        }

        // Calculate totals
        double totalMarks = marks.stream().mapToDouble(Mark::getMarks).sum();
        double percentage = totalMarks / marks.size();
        String grade = calculateGrade(percentage);

        Result result = new Result();
        result.setStudentId(studentId);
        result.setTotalMarks(totalMarks);
        result.setPercentage(Math.round(percentage * 100.0) / 100.0);
        result.setGrade(grade);
        result.setRankPosition(0); // Will be updated after all results are generated
        result.setPublished(false);

        Result saved = resultDAO.createOrUpdate(result);
        if (saved == null) {
            throw new RuntimeException("Failed to generate result for student ID: " + studentId);
        }

        // Recalculate all ranks
        resultDAO.updateRanks();

        LOGGER.info("Result generated for student: " + student.getName() + " (Percentage: " + percentage + "%, Grade: " + grade + ")");
        return resultDAO.findByStudentId(studentId);
    }

    /**
     * Generate results for ALL students that have marks.
     */
    public int generateAllResults() {
        List<Student> allStudents = studentDAO.findAll();
        int count = 0;

        for (Student student : allStudents) {
            List<Mark> marks = markDAO.findByStudentId(student.getStudentId());
            if (!marks.isEmpty()) {
                double totalMarks = marks.stream().mapToDouble(Mark::getMarks).sum();
                double percentage = totalMarks / marks.size();
                String grade = calculateGrade(percentage);

                Result result = new Result();
                result.setStudentId(student.getStudentId());
                result.setTotalMarks(totalMarks);
                result.setPercentage(Math.round(percentage * 100.0) / 100.0);
                result.setGrade(grade);
                result.setRankPosition(0);
                result.setPublished(false);

                resultDAO.createOrUpdate(result);
                count++;
            }
        }

        // Recalculate all ranks
        resultDAO.updateRanks();

        LOGGER.info("Generated results for " + count + " students");
        return count;
    }

    /**
     * Publish all results and notify students.
     */
    public void publishResults() {
        resultDAO.publishAll();

        // Send notifications to all students with results
        List<Result> results = resultDAO.findAll();
        for (Result result : results) {
            Notification notification = new Notification();
            notification.setStudentId(result.getStudentId());
            notification.setMessage("Your results have been published. Grade: " + result.getGrade() +
                    ", Rank: " + result.getRankPosition() + ", Percentage: " + result.getPercentage() + "%");
            notificationDAO.create(notification);
        }

        LOGGER.info("Results published and " + results.size() + " students notified");
    }

    /**
     * Get result for a specific student.
     */
    public Result getResultByStudentId(int studentId) {
        Result result = resultDAO.findByStudentId(studentId);
        if (result == null) {
            throw new ResourceNotFoundException("No result found for student ID: " + studentId);
        }
        return result;
    }

    /**
     * Get all results (rankings).
     */
    public List<Result> getAllResults() {
        return resultDAO.findAll();
    }

    /**
     * Get top N students.
     */
    public List<Result> getTopStudents(int limit) {
        return resultDAO.getTopStudents(limit);
    }

    /**
     * Get total results count.
     */
    public int getResultCount() {
        return resultDAO.count();
    }

    /**
     * Get number of passed students.
     */
    public int getPassedCount() {
        return resultDAO.countPassed();
    }

    /**
     * Get number of failed students.
     */
    public int getFailedCount() {
        return resultDAO.countFailed();
    }

    /**
     * Get count by specific grade.
     */
    public int getCountByGrade(String grade) {
        return resultDAO.countByGrade(grade);
    }

    /**
     * Calculate grade based on percentage.
     * 90+ = A+, 80-89 = A, 70-79 = B, 60-69 = C, Below 60 = F
     */
    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        return "F";
    }
}
