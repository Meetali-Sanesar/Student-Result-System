# 🚀 Deployment Guide

Production deployment guide for the Student Result Management System (SRMS).

---

## Overview

This guide covers deploying SRMS to a production Apache Tomcat 10.1+ server with a MySQL 8.x database backend.

**Architecture:**
```
Browser → Tomcat 10.1 (WAR) → MySQL 8.x
           └── Jakarta Servlet 5.0
           └── JDBC Connection Pool
```

---

## Step 1: Production Database Setup

### 1.1 Create a Dedicated Database User

Do **not** use `root` in production. Create a dedicated user with minimal privileges:

```sql
-- Connect as root
mysql -u root -p

-- Create production database
CREATE DATABASE IF NOT EXISTS student_result_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Create a dedicated user
CREATE USER 'srms_user'@'localhost' IDENTIFIED BY 'StrongP@ssw0rd!2024';

-- Grant only necessary privileges
GRANT SELECT, INSERT, UPDATE, DELETE ON student_result_db.* TO 'srms_user'@'localhost';
FLUSH PRIVILEGES;
```

### 1.2 Initialize Schema

```bash
mysql -u srms_user -p student_result_db < database/schema.sql
```

### 1.3 Load Initial Data (Optional)

```bash
# Load seed data for testing, or skip for a clean production database
mysql -u srms_user -p student_result_db < database/seed_data.sql
```

> **Important**: If you skip seed data, you must create at least one admin user manually:
> ```sql
> -- Generate BCrypt hash for your admin password using the application or an online tool
> INSERT INTO users (username, password, role)
> VALUES ('admin', '$2a$10$YOUR_BCRYPT_HASH_HERE', 'ADMIN');
> ```

---

## Step 2: Configure for Production

### 2.1 Database Configuration

Edit `src/main/resources/db.properties` before building:

```properties
# Production Database Configuration
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/student_result_db?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=false
db.username=srms_user
db.password=StrongP@ssw0rd!2024
db.pool.size=20
```

**Production changes:**
- `useSSL=true` — Enable SSL for database connections
- `allowPublicKeyRetrieval=false` — Disable for security
- `db.pool.size=20` — Increase pool for higher concurrency
- Use the dedicated `srms_user` account

### 2.2 Using Environment Variables

For sensitive configurations, you can use environment variables instead of hardcoding credentials. Set the following in your Tomcat environment:

```bash
# Linux: Add to /etc/systemd/system/tomcat.service or setenv.sh
export SRMS_DB_URL="jdbc:mysql://localhost:3306/student_result_db?useSSL=true&serverTimezone=UTC"
export SRMS_DB_USERNAME="srms_user"
export SRMS_DB_PASSWORD="StrongP@ssw0rd!2024"
export SRMS_DB_POOL_SIZE="20"
```

```bash
# Windows: Set system environment variables
setx SRMS_DB_URL "jdbc:mysql://localhost:3306/student_result_db?useSSL=true&serverTimezone=UTC"
setx SRMS_DB_USERNAME "srms_user"
setx SRMS_DB_PASSWORD "StrongP@ssw0rd!2024"
setx SRMS_DB_POOL_SIZE "20"
```

> **Note**: The current implementation reads from `db.properties`. To use environment variables, modify `DatabaseConnection.java` to check `System.getenv()` first, falling back to properties file values.

### 2.3 Enable Secure Cookies (HTTPS)

In `src/main/webapp/WEB-INF/web.xml`, set `<secure>` to `true`:

```xml
<session-config>
    <session-timeout>30</session-timeout>
    <cookie-config>
        <http-only>true</http-only>
        <secure>true</secure>  <!-- Enable for HTTPS -->
        <max-age>1800</max-age>
    </cookie-config>
    <tracking-mode>COOKIE</tracking-mode>
</session-config>
```

---

## Step 3: Build the WAR

### 3.1 Maven Build

```bash
# Clean build, skip tests
mvn clean package -DskipTests

# Or with tests
mvn clean package
```

The WAR file is generated at: `target/student-result-management-system.war`

### 3.2 Verify WAR Contents

```bash
# List WAR contents
jar tf target/student-result-management-system.war | head -30
```

Ensure it contains:
- `WEB-INF/web.xml`
- `WEB-INF/classes/com/srms/**/*.class`
- `WEB-INF/classes/db.properties`
- `WEB-INF/lib/*.jar` (dependencies)
- `css/style.css`
- `js/app.js`
- `pages/*.html`

---

## Step 4: Deploy to Tomcat

### 4.1 Stop Tomcat

```bash
# Linux/macOS
$CATALINA_HOME/bin/shutdown.sh

# Windows
%CATALINA_HOME%\bin\shutdown.bat
```

### 4.2 Remove Old Deployment (if upgrading)

```bash
# Linux/macOS
rm -rf $CATALINA_HOME/webapps/student-result-management-system*

# Windows
rmdir /s /q %CATALINA_HOME%\webapps\student-result-management-system
del %CATALINA_HOME%\webapps\student-result-management-system.war
```

### 4.3 Deploy WAR

```bash
# Linux/macOS
cp target/student-result-management-system.war $CATALINA_HOME/webapps/

# Windows
copy target\student-result-management-system.war %CATALINA_HOME%\webapps\
```

### 4.4 Start Tomcat

```bash
# Linux/macOS
$CATALINA_HOME/bin/startup.sh

# Windows
%CATALINA_HOME%\bin\startup.bat
```

### 4.5 Verify Deployment

Monitor Tomcat logs:
```bash
tail -f $CATALINA_HOME/logs/catalina.out
```

Look for:
```
INFO [main] org.apache.catalina.startup.HostConfig.deployWAR
  Deploying web application archive [.../student-result-management-system.war]
INFO [main] org.apache.catalina.startup.HostConfig.deployWAR
  Deployment of web application archive [...] has finished
```

---

## Step 5: Production Tomcat Configuration

### 5.1 Connector Settings (server.xml)

For production, configure the HTTP/HTTPS connector in `$CATALINA_HOME/conf/server.xml`:

```xml
<!-- HTTP Connector (redirect to HTTPS) -->
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443"
           maxThreads="200"
           minSpareThreads="25"
           acceptCount="100"
           compression="on"
           compressionMinSize="2048"
           compressableMimeType="text/html,text/xml,text/plain,text/css,
                                  application/javascript,application/json" />

<!-- HTTPS Connector -->
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
           maxThreads="200"
           SSLEnabled="true"
           scheme="https"
           secure="true">
    <SSLHostConfig>
        <Certificate certificateKeystoreFile="conf/keystore.jks"
                     certificateKeystorePassword="your_keystore_password"
                     type="RSA" />
    </SSLHostConfig>
</Connector>
```

### 5.2 JVM Options

Create `$CATALINA_HOME/bin/setenv.sh` (Linux) or `setenv.bat` (Windows):

```bash
# setenv.sh
export CATALINA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
export CATALINA_OPTS="$CATALINA_OPTS -Dfile.encoding=UTF-8"
```

```bat
:: setenv.bat
set CATALINA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC
set CATALINA_OPTS=%CATALINA_OPTS% -Dfile.encoding=UTF-8
```

### 5.3 Access Log (server.xml)

Enable access logging in `$CATALINA_HOME/conf/server.xml`:

```xml
<Valve className="org.apache.catalina.valves.AccessLogValve"
       directory="logs"
       prefix="access_log"
       suffix=".txt"
       pattern="%h %l %u %t &quot;%r&quot; %s %b %D" />
```

---

## Step 6: MySQL Production Configuration

### 6.1 Recommended my.cnf Settings

```ini
[mysqld]
# Character Set
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci

# InnoDB Settings
innodb_buffer_pool_size = 512M
innodb_log_file_size = 128M
innodb_flush_log_at_trx_commit = 1

# Connection Settings
max_connections = 150
wait_timeout = 600

# Query Cache (MySQL 8.0 removed this; skip if using 8.0+)
# query_cache_size = 64M

# Slow Query Log
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow-query.log
long_query_time = 2
```

### 6.2 Backup Strategy

Set up automated daily backups:

```bash
#!/bin/bash
# backup.sh — Run daily via cron
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups/mysql"
mkdir -p $BACKUP_DIR

mysqldump -u srms_user -p'StrongP@ssw0rd!2024' \
    --single-transaction \
    --routines \
    --triggers \
    student_result_db > $BACKUP_DIR/srms_backup_$TIMESTAMP.sql

# Keep only last 30 days
find $BACKUP_DIR -name "srms_backup_*.sql" -mtime +30 -delete
```

Add to crontab:
```bash
# Daily backup at 2:00 AM
0 2 * * * /path/to/backup.sh
```

---

## Post-Deployment Checklist

- [ ] Application loads at expected URL
- [ ] Admin login works with correct credentials
- [ ] Student login works
- [ ] Database operations (CRUD) function correctly
- [ ] PDF export generates correctly
- [ ] Excel export generates correctly
- [ ] File upload works (check write permissions on upload directory)
- [ ] HTTPS is configured (if applicable)
- [ ] Session cookies are secure and HTTP-only
- [ ] Error pages (404, 500) render properly
- [ ] Tomcat access logs are recording
- [ ] Database backups are scheduled
- [ ] `db.properties` does not contain default/weak passwords
