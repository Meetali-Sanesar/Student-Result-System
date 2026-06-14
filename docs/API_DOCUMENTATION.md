# 📡 API Documentation

Full REST API reference for the Student Result Management System (SRMS).

---

## Base URL

```
http://localhost:8080/student-result-management-system/api
```

## Response Format

All API responses follow a consistent JSON format:

```json
{
    "success": true,
    "data": { ... },
    "message": "Optional message"
}
```

### Error Response

```json
{
    "success": false,
    "message": "Error description"
}
```

### HTTP Status Codes

| Code | Meaning |
|------|---------|
| `200` | OK — Request succeeded |
| `201` | Created — Resource created successfully |
| `400` | Bad Request — Invalid input or validation error |
| `401` | Unauthorized — Not authenticated |
| `403` | Forbidden — Insufficient permissions |
| `404` | Not Found — Resource not found |
| `500` | Internal Server Error — Server-side failure |

---

## Authentication Flow

### Overview

SRMS uses session-based authentication. The flow is:

1. Client sends `POST /api/auth/login` with credentials
2. Server validates, creates session, returns user data
3. All subsequent requests include the session cookie automatically
4. Session expires after 30 minutes of inactivity
5. On 401 response, client redirects to login page

### Roles

| Role | Access |
|------|--------|
| `ADMIN` | Full CRUD on students, subjects, marks, results. Export files. View audit logs. |
| `STUDENT` | Read-only access to own profile, marks, results, notifications. Upload profile image. |

---

## Endpoints

---

### 🔐 Authentication

#### POST `/api/auth/login`

Authenticate a user and create a session.

**Request Body:**
```json
{
    "username": "admin",
    "password": "admin123"
}
```

**Success Response (200):**
```json
{
    "success": true,
    "data": {
        "user": {
            "userId": 1,
            "username": "admin",
            "role": "ADMIN",
            "studentId": null
        },
        "role": "ADMIN",
        "studentId": null
    },
    "message": "Login successful"
}
```

**Error Response (401):**
```json
{
    "success": false,
    "message": "Invalid username or password"
}
```

---

#### POST `/api/auth/logout`

Destroy the current session.

**Response (200):**
```json
{
    "success": true,
    "message": "Logout successful"
}
```

---

#### GET `/api/auth/me`

Get the currently authenticated user.

**Response (200):**
```json
{
    "success": true,
    "data": {
        "userId": 1,
        "username": "admin",
        "role": "ADMIN",
        "studentId": null
    }
}
```

**Error (401):**
```json
{
    "success": false,
    "message": "Not authenticated"
}
```

---

### 👨‍🎓 Students

> **Admin only** for create, update, delete. Students can read their own profile.

#### GET `/api/students`

List all students.

**Response (200):**
```json
{
    "success": true,
    "data": [
        {
            "studentId": 1,
            "name": "Aarav Sharma",
            "email": "aarav.sharma@university.edu",
            "phone": "9876543210",
            "department": "Computer Science",
            "profileImage": null,
            "createdAt": "2026-06-13T12:00:00.000+00:00"
        }
    ]
}
```

---

#### GET `/api/students/{id}`

Get a specific student by ID.

**Response (200):**
```json
{
    "success": true,
    "data": {
        "studentId": 1,
        "name": "Aarav Sharma",
        "email": "aarav.sharma@university.edu",
        "phone": "9876543210",
        "department": "Computer Science",
        "profileImage": null,
        "createdAt": "2026-06-13T12:00:00.000+00:00"
    }
}
```

---

#### POST `/api/students`

Create a new student (also creates a user account).

**Request Body:**
```json
{
    "name": "New Student",
    "email": "new.student@university.edu",
    "phone": "9876543299",
    "department": "Computer Science"
}
```

**Response (201):**
```json
{
    "success": true,
    "data": {
        "studentId": 16,
        "name": "New Student",
        "email": "new.student@university.edu",
        "phone": "9876543299",
        "department": "Computer Science"
    },
    "message": "Student created successfully"
}
```

---

#### PUT `/api/students/{id}`

Update an existing student.

**Request Body:**
```json
{
    "name": "Updated Name",
    "email": "updated@university.edu",
    "phone": "9876543299",
    "department": "Electrical Engineering"
}
```

**Response (200):**
```json
{
    "success": true,
    "data": { ... },
    "message": "Student updated successfully"
}
```

---

#### DELETE `/api/students/{id}`

Delete a student (cascades to marks, results, notifications).

**Response (200):**
```json
{
    "success": true,
    "message": "Student deleted successfully"
}
```

---

### 📚 Subjects

> **Admin only** for create, update, delete.

#### GET `/api/subjects`

List all subjects.

**Response (200):**
```json
{
    "success": true,
    "data": [
        {
            "subjectId": 1,
            "subjectName": "Mathematics",
            "subjectCode": "MATH101"
        }
    ]
}
```

---

#### GET `/api/subjects/{id}`

Get a specific subject.

---

#### POST `/api/subjects`

Create a new subject.

**Request Body:**
```json
{
    "subjectName": "Data Structures",
    "subjectCode": "CS201"
}
```

**Response (201):**
```json
{
    "success": true,
    "data": {
        "subjectId": 7,
        "subjectName": "Data Structures",
        "subjectCode": "CS201"
    },
    "message": "Subject created successfully"
}
```

---

#### PUT `/api/subjects/{id}`

Update a subject.

---

#### DELETE `/api/subjects/{id}`

Delete a subject (cascades to marks).

---

### ✏️ Marks

> **Admin only** for create, update, delete. Students can view their own marks.

#### GET `/api/marks/student/{studentId}`

Get all marks for a student.

**Response (200):**
```json
{
    "success": true,
    "data": [
        {
            "markId": 1,
            "studentId": 1,
            "subjectId": 1,
            "subjectName": "Mathematics",
            "subjectCode": "MATH101",
            "marks": 92.00
        }
    ]
}
```

---

#### POST `/api/marks`

Create or update a mark entry.

**Request Body:**
```json
{
    "studentId": 1,
    "subjectId": 1,
    "marks": 95.50
}
```

**Validation:** Marks must be between 0 and 100.

**Response (201):**
```json
{
    "success": true,
    "data": { ... },
    "message": "Mark saved successfully"
}
```

---

#### DELETE `/api/marks/{markId}`

Delete a mark entry.

---

### 📋 Results

> **Admin only** for generation and publishing. Students can view their own results.

#### GET `/api/results/{studentId}`

Get a student's result.

**Response (200):**
```json
{
    "success": true,
    "data": {
        "resultId": 1,
        "studentId": 4,
        "totalMarks": 554.50,
        "percentage": 92.42,
        "grade": "A+",
        "rankPosition": 1,
        "published": true,
        "studentName": "Sneha Reddy",
        "department": "Computer Science"
    }
}
```

---

#### GET `/api/results/rankings`

Get all results ordered by rank.

**Response (200):**
```json
{
    "success": true,
    "data": [
        {
            "resultId": 1,
            "studentId": 4,
            "studentName": "Sneha Reddy",
            "department": "Computer Science",
            "totalMarks": 554.50,
            "percentage": 92.42,
            "grade": "A+",
            "rankPosition": 1
        }
    ]
}
```

---

#### GET `/api/results/top-students?limit=10`

Get top N students by percentage.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `limit` | int | 10 | Number of top students to return |

---

#### POST `/api/results/generate/{studentId}`

Generate/recalculate result for a specific student.

**Response (201):**
```json
{
    "success": true,
    "data": {
        "resultId": 16,
        "studentId": 1,
        "totalMarks": 515.00,
        "percentage": 85.83,
        "grade": "A",
        "rankPosition": 5
    },
    "message": "Result generated successfully"
}
```

---

#### POST `/api/results/generate-all`

Generate results for all students.

**Response (201):**
```json
{
    "success": true,
    "data": {
        "count": 15
    },
    "message": "Results generated for 15 students"
}
```

---

#### POST `/api/results/publish`

Publish all results (makes them visible to students).

**Response (200):**
```json
{
    "success": true,
    "message": "Results published successfully"
}
```

---

### 📊 Dashboard

> **Admin only**

#### GET `/api/dashboard/stats`

Get dashboard statistics.

**Response (200):**
```json
{
    "success": true,
    "data": {
        "totalStudents": 15,
        "totalSubjects": 6,
        "totalResults": 15,
        "passPercentage": 80.0,
        "failedCount": 3,
        "topStudent": {
            "studentName": "Sneha Reddy",
            "department": "Computer Science",
            "percentage": 92.42,
            "grade": "A+"
        },
        "gradeDistribution": {
            "A+": 1,
            "A": 6,
            "B": 3,
            "C": 2,
            "F": 3
        },
        "departmentStats": {
            "Computer Science": { "studentCount": 5 },
            "Electrical Engineering": { "studentCount": 5 },
            "Mechanical Engineering": { "studentCount": 5 }
        }
    }
}
```

---

### 🔔 Notifications

> **Student only**

#### GET `/api/notifications`

Get notifications for the current student.

**Response (200):**
```json
{
    "success": true,
    "data": {
        "notifications": [
            {
                "notificationId": 1,
                "studentId": 1,
                "message": "Your results have been published. Grade: A, Rank: 5",
                "isRead": false,
                "createdAt": "2026-06-13T12:00:00.000+00:00"
            }
        ],
        "unreadCount": 1
    }
}
```

---

#### PUT `/api/notifications/{id}/read`

Mark a notification as read.

---

### 📝 Audit Logs

> **Admin only**

#### GET `/api/audit-logs`

Get audit log entries.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `limit` | int | 50 | Max number of logs to return |

**Response (200):**
```json
{
    "success": true,
    "data": [
        {
            "logId": 1,
            "adminId": 1,
            "action": "STUDENT_ADDED",
            "details": "Added student: Aarav Sharma (ID: 1)",
            "actionTime": "2026-06-13T12:00:00.000+00:00"
        }
    ]
}
```

**Tracked Actions:**
- `STUDENT_ADDED`, `STUDENT_UPDATED`, `STUDENT_DELETED`
- `SUBJECT_ADDED`, `SUBJECT_UPDATED`, `SUBJECT_DELETED`
- `MARKS_ENTERED`, `MARKS_UPDATED`, `MARKS_DELETED`
- `RESULT_GENERATED`, `RESULT_PUBLISHED`

---

### 📁 Files

#### POST `/api/files/upload-image`

Upload a student profile image.

**Content-Type:** `multipart/form-data`

**Form Fields:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `image` | file | Yes | Image file (JPEG, PNG, GIF, WebP) |
| `studentId` | int | Yes | Target student ID |

**Constraints:**
- Max file size: 5 MB
- Allowed types: `image/jpeg`, `image/png`, `image/gif`, `image/webp`

**Response (200):**
```json
{
    "success": true,
    "data": "abc123_profile.jpg",
    "message": "Profile image uploaded successfully"
}
```

---

#### GET `/api/files/result-pdf/{studentId}`

Download a student's result as a styled PDF.

**Response:** Binary PDF file (`application/pdf`)

**Headers:**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename=result_Student_Name.pdf
```

---

#### GET `/api/files/results-excel`

Download all results as an Excel spreadsheet.

**Response:** Binary XLSX file

**Headers:**
```
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename=student_results.xlsx
```

**Excel Columns:**
| Column | Description |
|--------|-------------|
| Rank | Student's class rank |
| Student ID | Unique student identifier |
| Student Name | Full name |
| Department | Academic department |
| Total Marks | Sum of all subject marks |
| Percentage (%) | Average percentage |
| Grade | Calculated grade (A+, A, B, C, F) |
| Status | PASSED (≥60%) or FAILED (<60%) |

---

## Grading Scale

| Percentage | Grade |
|------------|-------|
| 90% – 100% | A+ |
| 80% – 89% | A |
| 70% – 79% | B |
| 60% – 69% | C |
| Below 60% | F |

---

## Error Codes Reference

| Code | Endpoint | Description |
|------|----------|-------------|
| 400 | All POST/PUT | Validation error (missing fields, invalid data) |
| 400 | POST marks | Marks must be between 0 and 100 |
| 401 | All | Session expired or not authenticated |
| 403 | Admin endpoints | Student trying to access admin features |
| 404 | GET by ID | Student, subject, result, or mark not found |
| 404 | GET result-pdf | No result found for student |
| 500 | All | Unexpected server error |
