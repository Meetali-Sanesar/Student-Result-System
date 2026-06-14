package com.srms.service;

import com.srms.dao.MarkDAO;
import com.srms.dao.StudentDAO;
import com.srms.dao.SubjectDAO;
import com.srms.exception.ResourceNotFoundException;
import com.srms.exception.ValidationException;
import com.srms.model.Mark;
import com.srms.security.InputValidator;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service for marks management operations.
 */
public class MarkService {

    private static final Logger LOGGER = Logger.getLogger(MarkService.class.getName());
    private final MarkDAO markDAO = new MarkDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    /**
     * Enter marks for a student in a subject.
     */
    public Mark addMark(Mark mark) {
        // Validate student exists
        if (studentDAO.findById(mark.getStudentId()) == null) {
            throw new ResourceNotFoundException("Student not found with ID: " + mark.getStudentId());
        }
        // Validate subject exists
        if (subjectDAO.findById(mark.getSubjectId()) == null) {
            throw new ResourceNotFoundException("Subject not found with ID: " + mark.getSubjectId());
        }
        // Validate marks range
        InputValidator.validateMarks(mark.getMarks());

        // Check for duplicate
        Mark existing = markDAO.findByStudentAndSubject(mark.getStudentId(), mark.getSubjectId());
        if (existing != null) {
            throw new ValidationException("Marks already exist for this student-subject combination. Use update instead.");
        }

        Mark created = markDAO.create(mark);
        if (created == null) {
            throw new RuntimeException("Failed to create mark");
        }
        LOGGER.info("Mark added: Student " + mark.getStudentId() + ", Subject " + mark.getSubjectId() + ", Marks " + mark.getMarks());
        return created;
    }

    /**
     * Update existing marks.
     */
    public Mark updateMark(int markId, double newMarks) {
        Mark existing = markDAO.findById(markId);
        if (existing == null) {
            throw new ResourceNotFoundException("Mark not found with ID: " + markId);
        }
        InputValidator.validateMarks(newMarks);

        existing.setMarks(newMarks);
        boolean updated = markDAO.update(existing);
        if (!updated) {
            throw new RuntimeException("Failed to update mark");
        }
        LOGGER.info("Mark updated: ID " + markId + ", New marks: " + newMarks);
        return markDAO.findById(markId);
    }

    /**
     * Delete a mark.
     */
    public void deleteMark(int markId) {
        Mark existing = markDAO.findById(markId);
        if (existing == null) {
            throw new ResourceNotFoundException("Mark not found with ID: " + markId);
        }
        boolean deleted = markDAO.delete(markId);
        if (!deleted) {
            throw new RuntimeException("Failed to delete mark");
        }
        LOGGER.info("Mark deleted: ID " + markId);
    }

    /**
     * Get all marks for a student.
     */
    public List<Mark> getMarksByStudentId(int studentId) {
        if (studentDAO.findById(studentId) == null) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        return markDAO.findByStudentId(studentId);
    }

    /**
     * Get all marks.
     */
    public List<Mark> getAllMarks() {
        return markDAO.findAll();
    }

    /**
     * Get a single mark by ID.
     */
    public Mark getMark(int markId) {
        Mark mark = markDAO.findById(markId);
        if (mark == null) {
            throw new ResourceNotFoundException("Mark not found with ID: " + markId);
        }
        return mark;
    }
}
