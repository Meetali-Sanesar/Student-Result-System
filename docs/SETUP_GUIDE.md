# 🛠️ Setup Guide

Complete setup instructions for the Student Result Management System (SRMS).

---

## Prerequisites

Ensure the following software is installed on your development machine:

| Software | Version | Purpose |
|----------|---------|---------|
| **JDK** | 17+ | Java compilation and runtime |
| **Apache Maven** | 3.8+ | Build and dependency management |
| **MySQL Server** | 8.0+ | Database |
| **Apache Tomcat** | 10.1+ | Jakarta Servlet 5.0 container |

### Verify Installations

```bash
java -version        # Should show: openjdk 17.x.x or similar
mvn -version         # Should show: Apache Maven 3.8.x or later
mysql --version      # Should show: mysql Ver 8.x.x
```

---

## Step 1: Database Setup

### 1.1 Start MySQL Server

```bash
# Linux / macOS
sudo systemctl start mysql

# Windows (if MySQL is installed as a service)
net start mysql
```

### 1.2 Create Database and Tables

Connect to MySQL and execute the schema script:

```bash
mysql -u root -p < database/schema.sql
```

This creates:
- Database: `student_result_db`
- Tables: `users`, `students`, `subjects`, `marks`, `results`, `audit_logs`, `notifications`
- Indexes and foreign key constraints

### 1.3 Load Sample Data (Optional but Recommended)

```bash
mysql -u root -p < database/seed_data.sql
```

This populates:
- 1 admin user (`admin` / `admin123`)
- 15 students across 3 departments (Computer Science, Electrical Engineering, Mechanical Engineering)
- 15 student user accounts (all with password `student123`)
- 6 subjects (Mathematics, Physics, Chemistry, English, Computer Programming, Engineering Drawing)
- 90 mark entries (6 subjects × 15 students)
- 15 pre-calculated results with grades and rankings
- 6 sample notifications
- 6 sample audit log entries

### 1.4 Verify Database

```sql
mysql -u root -p
USE student_result_db;
SHOW TABLES;
SELECT COUNT(*) FROM students;   -- Should return 15
SELECT COUNT(*) FROM subjects;   -- Should return 6
SELECT COUNT(*) FROM marks;      -- Should return 90
SELECT COUNT(*) FROM results;    -- Should return 15
```

---

## Step 2: Application Configuration

### 2.1 Database Connection

Edit the file `src/main/resources/db.properties`:

```properties
# Database Configuration
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/student_result_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=your_mysql_password
db.pool.size=10
```

| Property | Description |
|----------|-------------|
| `db.driver` | MySQL JDBC driver class (do not change) |
| `db.url` | JDBC URL with database name, SSL, and timezone settings |
| `db.username` | MySQL username |
| `db.password` | MySQL password |
| `db.pool.size` | Number of connections in the connection pool (default: 10) |

> **Important**: If your MySQL server uses a different port or hostname, update `db.url` accordingly.

### 2.2 Session Configuration

Session timeout is configured in `src/main/webapp/WEB-INF/web.xml`:

```xml
<session-config>
    <session-timeout>30</session-timeout>  <!-- 30 minutes -->
    <cookie-config>
        <http-only>true</http-only>
        <secure>false</secure>  <!-- Set to true in production with HTTPS -->
    </cookie-config>
</session-config>
```

### 2.3 File Upload Configuration

Also in `web.xml`, the maximum file upload sizes are configured:

```xml
<multipart-config>
    <max-file-size>5242880</max-file-size>         <!-- 5 MB per file -->
    <max-request-size>10485760</max-request-size>  <!-- 10 MB total -->
    <file-size-threshold>1048576</file-size-threshold> <!-- 1 MB memory threshold -->
</multipart-config>
```

---

## Step 3: Build the Application

### 3.1 Build with Maven

From the project root directory:

```bash
# Full build (skip tests for initial setup)
mvn clean package -DskipTests
```

This produces: `target/student-result-management-system.war`

### 3.2 Run Tests (Optional)

```bash
mvn test
```

---

## Step 4: Deploy to Tomcat

### 4.1 Configure Tomcat

Ensure Tomcat 10.1+ is installed and `CATALINA_HOME` is set:

```bash
# Check Tomcat version
$CATALINA_HOME/bin/version.sh     # Linux/macOS
%CATALINA_HOME%\bin\version.bat   # Windows
```

### 4.2 Deploy the WAR

Copy the WAR file to Tomcat's `webapps` directory:

```bash
# Linux/macOS
cp target/student-result-management-system.war $CATALINA_HOME/webapps/

# Windows
copy target\student-result-management-system.war %CATALINA_HOME%\webapps\
```

### 4.3 Start Tomcat

```bash
# Linux/macOS
$CATALINA_HOME/bin/startup.sh

# Windows
%CATALINA_HOME%\bin\startup.bat
```

### 4.4 Verify Deployment

1. Check Tomcat logs for successful deployment:
   ```bash
   tail -f $CATALINA_HOME/logs/catalina.out
   ```
   Look for: `Deployment of web application archive [...] has finished`

2. Open browser and navigate to:
   ```
   http://localhost:8080/student-result-management-system/
   ```

3. You should be redirected to the login page.

---

## Step 5: Test the Application

### Admin Login
- **URL**: `http://localhost:8080/student-result-management-system/pages/login.html`
- **Username**: `admin`
- **Password**: `admin123`

### Student Login
- **Username**: `aarav.sharma` (or any other student username)
- **Password**: `student123`

### Verification Checklist

| Test | Expected Result |
|------|----------------|
| Admin login | Redirects to Admin Dashboard with stats |
| Student login | Redirects to Student Dashboard with profile |
| Add Student | New student appears in list |
| Edit Student | Updates reflected immediately |
| Delete Student | Student removed with cascade (marks, results) |
| Add Subject | New subject appears in list |
| Enter Marks | Marks saved with 0-100 validation |
| Generate Results | Results calculated with grade and rank |
| Publish Results | Results visible to students |
| Download PDF | PDF opens with formatted result report |
| Export Excel | `.xlsx` file downloads with all results |
| Profile Image Upload | Image appears in profile and dashboard |
| Logout | Session destroyed, redirect to login |
| Session Timeout | Auto-redirect to login after 30 min inactivity |

---

## Troubleshooting

### Database Connection Error
```
java.sql.SQLException: Access denied for user 'root'@'localhost'
```
**Fix**: Verify `db.username` and `db.password` in `db.properties`.

### ClassNotFoundException: com.mysql.cj.jdbc.Driver
**Fix**: Ensure `mysql-connector-j` is in `pom.xml` dependencies and the WAR was built correctly with `mvn clean package`.

### 404 Error on API Calls
**Fix**: Ensure the context path in the URL matches the WAR filename. The default is `/student-result-management-system/`.

### File Upload Fails
**Fix**: Check that Tomcat has write permissions to the `uploads` directory inside the deployed webapp folder.

### Tomcat Won't Start (Port Conflict)
**Fix**: Check if port 8080 is in use:
```bash
# Linux/macOS
lsof -i :8080

# Windows
netstat -ano | findstr :8080
```
Change the port in `$CATALINA_HOME/conf/server.xml` if needed.
