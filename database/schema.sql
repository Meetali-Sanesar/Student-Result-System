-- =============================================
-- Student Result Management System
-- Database Schema
-- =============================================

CREATE DATABASE IF NOT EXISTS student_result_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE student_result_db;

-- =============================================
-- Table: users
-- Stores login credentials for admin and student
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    user_id      INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    role         ENUM('ADMIN', 'STUDENT') NOT NULL,
    student_id   INT DEFAULT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remember_token VARCHAR(255) DEFAULT NULL,
    INDEX idx_users_role (role),
    INDEX idx_users_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: students
-- Core student information
-- =============================================
CREATE TABLE IF NOT EXISTS students (
    student_id    INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    phone         VARCHAR(15),
    department    VARCHAR(50)  NOT NULL,
    profile_image VARCHAR(255) DEFAULT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_students_department (department),
    INDEX idx_students_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: subjects
-- Available subjects/courses
-- =============================================
CREATE TABLE IF NOT EXISTS subjects (
    subject_id   INT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20)  NOT NULL UNIQUE,
    INDEX idx_subjects_code (subject_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: marks
-- Student marks per subject
-- =============================================
CREATE TABLE IF NOT EXISTS marks (
    mark_id    INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    marks      DECIMAL(5,2) NOT NULL,
    CONSTRAINT chk_marks_range CHECK (marks >= 0 AND marks <= 100),
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_marks_subject FOREIGN KEY (subject_id)
        REFERENCES subjects(subject_id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_subject (student_id, subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: results
-- Calculated results per student
-- =============================================
CREATE TABLE IF NOT EXISTS results (
    result_id   INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT NOT NULL UNIQUE,
    total_marks DECIMAL(7,2),
    percentage  DECIMAL(5,2),
    grade       VARCHAR(5),
    rank_position INT,
    published   BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_results_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: audit_logs
-- Tracks all admin activities
-- =============================================
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id      INT AUTO_INCREMENT PRIMARY KEY,
    admin_id    INT NOT NULL,
    action      VARCHAR(255) NOT NULL,
    details     TEXT,
    action_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_admin FOREIGN KEY (admin_id)
        REFERENCES users(user_id),
    INDEX idx_audit_time (action_time),
    INDEX idx_audit_admin (admin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: notifications
-- Student notification messages
-- =============================================
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT NOT NULL,
    message         VARCHAR(500) NOT NULL,
    is_read         BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE,
    INDEX idx_notification_student (student_id),
    INDEX idx_notification_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add FK from users.student_id to students.student_id
ALTER TABLE users
    ADD CONSTRAINT fk_users_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE SET NULL;
