package com.srms.model;

/**
 * Represents a subject/course.
 */
public class Subject {

    private int subjectId;
    private String subjectName;
    private String subjectCode;

    public Subject() {}

    public Subject(int subjectId, String subjectName, String subjectCode) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }

    // Getters and Setters
    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    @Override
    public String toString() {
        return "Subject{subjectId=" + subjectId + ", subjectName='" + subjectName + "', code='" + subjectCode + "'}";
    }
}
