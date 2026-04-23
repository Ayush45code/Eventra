# Eventra Backend Setup Guide

## Quick Test Results ✅
- Java 21 is installed and working
- Basic Java compilation successful
- Project structure is correct

## Next Steps to Test the Backend

### Step 1: Install Apache Tomcat

Download and install Apache Tomcat 9.0 or higher:

1. **Download Tomcat**: https://tomcat.apache.org/download-90.cgi
2. **Extract** to a folder (e.g., `C:\tomcat9`)
3. **Set CATALINA_HOME environment variable** (optional but recommended)

### Step 2: Install MySQL Database

1. **Download MySQL**: https://dev.mysql.com/downloads/mysql/
2. **Install MySQL Server**
3. **Start MySQL service**
4. **Create database** using our schema:

```sql
-- Run this in MySQL command line or MySQL Workbench
source database_schema.sql;
```

### Step 3: Get Required JAR Files

Create a `lib` folder and download these files:

1. **Servlet API**: https://repo1.maven.org/maven2/javax/servlet/javax.servlet-api/4.0.1/javax.servlet-api-4.0.1.jar
2. **MySQL Connector**: https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar  
3. **Gson**: https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar

### Step 4: Update Database Credentials

Edit `src/main/java/com/eventra/util/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/eventra_db";
private static final String USERNAME = "your_mysql_username";  // Change this
private static final String PASSWORD = "your_mysql_password";  // Change this
```

### Step 5: Compile the Project

Run the build script:

```bash
build.bat
```

Or compile manually:

```bash
javac -cp "lib/*" -d target\classes src\main\java\com\eventra\*.java src\main\java\com\eventra\*\*.java src\main\java\com\eventra\*\*\*.java
```

### Step 6: Deploy to Tomcat

1. **Copy files to Tomcat**:
   ```
   xcopy target\classes C:\tomcat9\webapps\eventra\WEB-INF\classes /E /I
   xcopy src\main\webapp\WEB-INF C:\tomcat9\webapps\eventra\WEB-INF /E /I
   xcopy lib C:\tomcat9\webapps\eventra\WEB-INF\lib /E /I
   ```

2. **Start Tomcat**:
   ```
   C:\tomcat9\bin\startup.bat
   ```

### Step 7: Test the Backend

Open your browser and test these URLs:

1. **Test endpoint**: http://localhost:8080/eventra/api/test
2. **Get all events**: http://localhost:8080/eventra/api/events
3. **Register user**: http://localhost:8080/eventra/api/register (POST with form data)

## Expected Test Results

### Test Endpoint Response
```json
{
  "success": true,
  "message": "Backend is working!",
  "timestamp": "Wed Apr 23 14:55:00 IST 2026"
}
```

### Sample API Calls

**Register a user** (using curl or Postman):
```bash
curl -X POST http://localhost:8080/eventra/api/register \
  -d "username=testuser&email=test@example.com&password=password123&fullName=Test User&phone=1234567890"
```

**Login**:
```bash
curl -X POST http://localhost:8080/eventra/api/login \
  -d "username=testuser&password=password123"
```

**Get Events**:
```bash
curl -X GET http://localhost:8080/eventra/api/events
```

## Troubleshooting

### Common Issues:

1. **404 Error**: Check if Tomcat is running and WAR is deployed correctly
2. **Database Connection Error**: Verify MySQL is running and credentials are correct
3. **Compilation Error**: Ensure all JAR files are in the `lib` folder
4. **CORS Error**: Check browser console, may need to adjust CORS settings

### Check Tomcat Status:
- Open http://localhost:8080/ - should show Tomcat homepage
- Check Tomcat logs in `C:\tomcat9\logs\catalina.out`

### Check Database:
```bash
mysql -u root -p
show databases;
use eventra_db;
show tables;
```

## Ready to Test!

Once you complete these steps, you'll have a fully functional Java backend using Apache Tomcat and JDBC as required by your teacher.

The backend includes:
- ✅ User authentication (register/login/logout)
- ✅ Event management (CRUD operations)
- ✅ Event registration system
- ✅ User profile management
- ✅ JSON API responses
- ✅ Database connectivity with JDBC
- ✅ Session management
- ✅ Error handling

Let me know which step you'd like help with first!
