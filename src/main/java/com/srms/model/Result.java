package com.srms.model;

import java.sql.Timestamp;

/**
 * Represents calculated academic result for a student.
 */
public class Result {

    private int resultId;
    private int studentId;
    private double totalMarks;
    private double percentage;
    private String grade;
    private int rankPosition;
    private boolean published;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Transient fields for display
    private String studentName;
    private String department;

    public Result() {}

    public Result(int resultId, int studentId, double totalMarks, double percentage,
                  String grade, int rankPosition, boolean published) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.grade = grade;
        this.rankPosition = rankPosition;
        this.published = published;
    }

    // Getters and Setters
    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double totalMarks) { this.totalMarks = totalMarks; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public int getRankPosition() { return rankPosition; }
    public void setRankPosition(int rankPosition) { this.rankPosition = rankPosition; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "Result{resultId=" + resultId + ", studentId=" + studentId +
               ", percentage=" + percentage + ", grade='" + grade + "', rank=" + rankPosition + "}";
    }
}
