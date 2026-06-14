package com.srms.model;

/**
 * Represents marks scored by a student in a subject.
 */
public class Mark {

    private int markId;
    private int studentId;
    private int subjectId;
    private double marks;

    // Transient fields for display purposes (not stored in marks table)
    private String studentName;
    private String subjectName;
    private String subjectCode;

    public Mark() {}

    public Mark(int markId, int studentId, int subjectId, double marks) {
        this.markId = markId;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.marks = marks;
    }

    // Getters and Setters
    public int getMarkId() { return markId; }
    public void setMarkId(int markId) { this.markId = markId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    @Override
    public String toString() {
        return "Mark{markId=" + markId + ", studentId=" + studentId +
               ", subjectId=" + subjectId + ", marks=" + marks + "}";
    }
}
