package com.srms.service;

import com.srms.dao.SubjectDAO;
import com.srms.exception.ResourceNotFoundException;
import com.srms.exception.ValidationException;
import com.srms.model.Subject;
import com.srms.security.InputValidator;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service for subject management operations.
 */
public class SubjectService {

    private static final Logger LOGGER = Logger.getLogger(SubjectService.class.getName());
    private final SubjectDAO subjectDAO = new SubjectDAO();

    public Subject addSubject(Subject subject) {
        subject.setSubjectName(InputValidator.validateRequired(subject.getSubjectName(), "Subject name"));
        subject.setSubjectCode(InputValidator.validateSubjectCode(subject.getSubjectCode()));

        // Check for duplicate code
        Subject existing = subjectDAO.findByCode(subject.getSubjectCode());
        if (existing != null) {
            throw new ValidationException("Subject code '" + subject.getSubjectCode() + "' already exists");
        }

        Subject created = subjectDAO.create(subject);
        if (created == null) {
            throw new RuntimeException("Failed to create subject");
        }
        LOGGER.info("Subject added: " + created.getSubjectName() + " (" + created.getSubjectCode() + ")");
        return created;
    }

    public Subject updateSubject(int subjectId, Subject subject) {
        Subject existing = subjectDAO.findById(subjectId);
        if (existing == null) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }

        subject.setSubjectName(InputValidator.validateRequired(subject.getSubjectName(), "Subject name"));
        subject.setSubjectCode(InputValidator.validateSubjectCode(subject.getSubjectCode()));

        // Check code uniqueness if changed
        if (!existing.getSubjectCode().equals(subject.getSubjectCode())) {
            Subject codeCheck = subjectDAO.findByCode(subject.getSubjectCode());
            if (codeCheck != null) {
                throw new ValidationException("Subject code '" + subject.getSubjectCode() + "' already exists");
            }
        }

        subject.setSubjectId(subjectId);
        boolean updated = subjectDAO.update(subject);
        if (!updated) {
            throw new RuntimeException("Failed to update subject");
        }
        LOGGER.info("Subject updated: " + subject.getSubjectName() + " (ID: " + subjectId + ")");
        return subjectDAO.findById(subjectId);
    }

    public void deleteSubject(int subjectId) {
        Subject existing = subjectDAO.findById(subjectId);
        if (existing == null) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }
        boolean deleted = subjectDAO.delete(subjectId);
        if (!deleted) {
            throw new RuntimeException("Failed to delete subject");
        }
        LOGGER.info("Subject deleted: " + existing.getSubjectName() + " (ID: " + subjectId + ")");
    }

    public Subject getSubject(int subjectId) {
        Subject subject = subjectDAO.findById(subjectId);
        if (subject == null) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }
        return subject;
    }

    public List<Subject> getAllSubjects() {
        return subjectDAO.findAll();
    }

    public int getSubjectCount() {
        return subjectDAO.count();
    }
}
