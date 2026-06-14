-- =============================================
-- Student Result Management System
-- Seed Data for Development & Testing
-- =============================================
-- Run AFTER schema.sql

USE student_result_db;

-- =============================================
-- Admin User
-- Username: admin  |  Password: admin123
-- BCrypt hash generated for 'admin123'
-- =============================================
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');

-- =============================================
-- Students (15 students across 3 departments)
-- =============================================
INSERT INTO students (name, email, phone, department) VALUES
('Aarav Sharma',     'aarav.sharma@university.edu',     '9876543210', 'Computer Science'),
('Priya Patel',      'priya.patel@university.edu',      '9876543211', 'Computer Science'),
('Rahul Kumar',      'rahul.kumar@university.edu',      '9876543212', 'Computer Science'),
('Sneha Reddy',      'sneha.reddy@university.edu',      '9876543213', 'Computer Science'),
('Vikram Singh',     'vikram.singh@university.edu',     '9876543214', 'Computer Science'),
('Ananya Iyer',      'ananya.iyer@university.edu',      '9876543215', 'Electrical Engineering'),
('Deepak Verma',     'deepak.verma@university.edu',     '9876543216', 'Electrical Engineering'),
('Kavitha Nair',     'kavitha.nair@university.edu',     '9876543217', 'Electrical Engineering'),
('Suresh Gupta',     'suresh.gupta@university.edu',     '9876543218', 'Electrical Engineering'),
('Meera Joshi',      'meera.joshi@university.edu',      '9876543219', 'Electrical Engineering'),
('Arjun Malhotra',   'arjun.malhotra@university.edu',   '9876543220', 'Mechanical Engineering'),
('Divya Saxena',     'divya.saxena@university.edu',     '9876543221', 'Mechanical Engineering'),
('Karthik Rajan',    'karthik.rajan@university.edu',    '9876543222', 'Mechanical Engineering'),
('Pooja Agarwal',    'pooja.agarwal@university.edu',    '9876543223', 'Mechanical Engineering'),
('Nikhil Deshmukh',  'nikhil.deshmukh@university.edu',  '9876543224', 'Mechanical Engineering');

-- =============================================
-- Student User Accounts
-- Default password for all students: student123
-- BCrypt hash generated for 'student123'
-- =============================================
INSERT INTO users (username, password, role, student_id) VALUES
('aarav.sharma',    '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 1),
('priya.patel',     '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 2),
('rahul.kumar',     '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 3),
('sneha.reddy',     '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 4),
('vikram.singh',    '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 5),
('ananya.iyer',     '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 6),
('deepak.verma',    '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 7),
('kavitha.nair',    '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 8),
('suresh.gupta',    '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 9),
('meera.joshi',     '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 10),
('arjun.malhotra',  '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 11),
('divya.saxena',    '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 12),
('karthik.rajan',   '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 13),
('pooja.agarwal',   '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 14),
('nikhil.deshmukh', '$2a$10$dXJ3SW6G7P50lGmMQoesjOuPm1WINbJU0bo0lBCSTjAFPVkBrOKGe', 'STUDENT', 15);

-- =============================================
-- Subjects (6 core subjects)
-- =============================================
INSERT INTO subjects (subject_name, subject_code) VALUES
('Mathematics',            'MATH101'),
('Physics',                'PHY102'),
('Chemistry',              'CHEM103'),
('English',                'ENG104'),
('Computer Programming',   'CS105'),
('Engineering Drawing',    'ED106');

-- =============================================
-- Marks (sample marks for all 15 students)
-- =============================================
INSERT INTO marks (student_id, subject_id, marks) VALUES
-- Aarav Sharma (CS)
(1, 1, 92.00), (1, 2, 88.50), (1, 3, 76.00), (1, 4, 85.00), (1, 5, 95.00), (1, 6, 78.50),
-- Priya Patel (CS)
(2, 1, 88.00), (2, 2, 91.00), (2, 3, 82.00), (2, 4, 90.00), (2, 5, 87.50), (2, 6, 84.00),
-- Rahul Kumar (CS)
(3, 1, 65.00), (3, 2, 58.00), (3, 3, 62.00), (3, 4, 70.00), (3, 5, 72.50), (3, 6, 55.00),
-- Sneha Reddy (CS)
(4, 1, 95.00), (4, 2, 93.50), (4, 3, 91.00), (4, 4, 88.00), (4, 5, 97.00), (4, 6, 90.00),
-- Vikram Singh (CS)
(5, 1, 45.00), (5, 2, 52.00), (5, 3, 48.00), (5, 4, 55.00), (5, 5, 60.00), (5, 6, 42.00),
-- Ananya Iyer (EE)
(6, 1, 78.00), (6, 2, 82.50), (6, 3, 75.00), (6, 4, 80.00), (6, 5, 70.00), (6, 6, 77.00),
-- Deepak Verma (EE)
(7, 1, 85.00), (7, 2, 80.00), (7, 3, 88.00), (7, 4, 75.00), (7, 5, 82.00), (7, 6, 79.00),
-- Kavitha Nair (EE)
(8, 1, 90.00), (8, 2, 86.00), (8, 3, 92.00), (8, 4, 88.50), (8, 5, 85.00), (8, 6, 91.00),
-- Suresh Gupta (EE)
(9, 1, 55.00), (9, 2, 60.00), (9, 3, 50.00), (9, 4, 58.00), (9, 5, 62.00), (9, 6, 48.00),
-- Meera Joshi (EE)
(10, 1, 72.00), (10, 2, 68.00), (10, 3, 74.00), (10, 4, 70.00), (10, 5, 65.00), (10, 6, 71.00),
-- Arjun Malhotra (ME)
(11, 1, 82.00), (11, 2, 78.00), (11, 3, 85.00), (11, 4, 80.00), (11, 5, 76.00), (11, 6, 83.00),
-- Divya Saxena (ME)
(12, 1, 91.00), (12, 2, 89.00), (12, 3, 87.50), (12, 4, 93.00), (12, 5, 90.00), (12, 6, 88.00),
-- Karthik Rajan (ME)
(13, 1, 68.00), (13, 2, 72.00), (13, 3, 65.00), (13, 4, 60.00), (13, 5, 70.00), (13, 6, 63.00),
-- Pooja Agarwal (ME)
(14, 1, 76.00), (14, 2, 80.00), (14, 3, 78.00), (14, 4, 82.00), (14, 5, 75.00), (14, 6, 79.00),
-- Nikhil Deshmukh (ME)
(15, 1, 40.00), (15, 2, 35.00), (15, 3, 42.00), (15, 4, 50.00), (15, 5, 38.00), (15, 6, 45.00);

-- =============================================
-- Pre-generated Results
-- =============================================
INSERT INTO results (student_id, total_marks, percentage, grade, rank_position, published) VALUES
(4,  554.50, 92.42, 'A+', 1,  TRUE),
(12, 538.50, 89.75, 'A',  2,  TRUE),
(8,  532.50, 88.75, 'A',  3,  TRUE),
(2,  522.50, 87.08, 'A',  4,  TRUE),
(1,  515.00, 85.83, 'A',  5,  TRUE),
(7,  489.00, 81.50, 'A',  6,  TRUE),
(11, 484.00, 80.67, 'A',  7,  TRUE),
(14, 470.00, 78.33, 'B',  8,  TRUE),
(6,  462.50, 77.08, 'B',  9,  TRUE),
(10, 420.00, 70.00, 'B',  10, TRUE),
(13, 398.00, 66.33, 'C',  11, TRUE),
(3,  382.50, 63.75, 'C',  12, TRUE),
(9,  333.00, 55.50, 'F',  13, TRUE),
(5,  302.00, 50.33, 'F',  14, TRUE),
(15, 250.00, 41.67, 'F',  15, TRUE);

-- =============================================
-- Sample Notifications
-- =============================================
INSERT INTO notifications (student_id, message, is_read) VALUES
(1,  'Your results have been published. Grade: A, Rank: 5', FALSE),
(2,  'Your results have been published. Grade: A, Rank: 4', FALSE),
(4,  'Your results have been published. Grade: A+, Rank: 1. Congratulations!', FALSE),
(5,  'Your results have been published. Grade: F, Rank: 14', TRUE),
(8,  'Your results have been published. Grade: A, Rank: 3', FALSE),
(12, 'Your results have been published. Grade: A, Rank: 2', FALSE);

-- =============================================
-- Sample Audit Logs
-- =============================================
INSERT INTO audit_logs (admin_id, action, details) VALUES
(1, 'STUDENT_ADDED',    'Added student: Aarav Sharma (ID: 1)'),
(1, 'STUDENT_ADDED',    'Added student: Priya Patel (ID: 2)'),
(1, 'SUBJECT_ADDED',    'Added subject: Mathematics (MATH101)'),
(1, 'MARKS_ENTERED',    'Entered marks for student ID 1'),
(1, 'RESULT_GENERATED', 'Generated results for all students'),
(1, 'RESULT_PUBLISHED', 'Published results for all students');
