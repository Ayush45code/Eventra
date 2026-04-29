# Eventra — College Event Management

A simple college event management app built with Apache Tomcat 11 + PostgreSQL +  HTML/CSS/JS.

## What it does
- Browse upcoming college events
- Register and log in
- Sign up for events
- Admin dashboard with live stats

## Prerequisites
- Java 11 or higher
- Apache Tomcat 11
- PostgreSQL (running on your network)

## Database Setup

Connect to your PostgreSQL server and run the schema file:

```bash
psql -U 24bcsf31 -d cse_db24 -f database_schema.sql
```

Database connection is configured in:
```
src/main/java/com/eventra/util/DatabaseConnection.java
```
```java
private static final String URL      = "jdbc:postgresql://192.168.1.17:5432/cse_db24";
private static final String USERNAME = "24bcsf31";
private static final String PASSWORD = "XXXXXXXX";
```
Change the IP to `localhost` if PostgreSQL is running on your own machine.

## Deploy to Tomcat

**Step 1 — Fix servlet imports**
```powershell
(Get-Content "D:\Eventra\src\main\java\com\eventra\servlet\ApiServlet.java") -replace 'javax.servlet', 'jakarta.servlet' | Set-Content "D:\Eventra\src\main\java\com\eventra\servlet\ApiServlet.java"
```

**Step 2 — Copy frontend files to tomcat**

**Step 3 — Compile Java files**


**Step 4 — Copy compiled classes**

**Step 5 — Restart Tomcat**
```powershell
& "C:\Program Files\Apache Software Foundation\Tomcat 11.0\bin\shutdown.bat"
Start-Sleep -Seconds 3
& "C:\Program Files\Apache Software Foundation\Tomcat 11.0\bin\startup.bat"
```

**Step 6 — Open in browser**
```
http://localhost:8008/eventra/index.html
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Check backend is running |
| GET | `/api/events` | Get all events |
| GET | `/api/stats` | Get user/event/registration counts |
| GET | `/api/registrations?email=` | Get registrations by email |
| POST | `/api/register` | Create new account |
| POST | `/api/login` | Login |
| POST | `/api/event-registration` | Register for an event |
| POST | `/api/profile` | Update profile |

## Project Structure

```
src/main/
├── java/com/eventra/
│   ├── model/        User.java, Event.java
│   ├── dao/          UserDAO.java
│   ├── servlet/      ApiServlet.java
│   └── util/         DatabaseConnection.java
└── webapp/
    ├── index.html   Main app
    ├── auth.html            Login / Register
    └── WEB-INF/
        ├── web.xml
        └── lib/             gson.jar, postgresql.jar
```

## Database Tables

- **users** — id, username, full_name, email, password, department, roll_number, created_at
- **events** — id, title, category, event_date, location, description, created_at
- **registrations** — id, full_name, email, phone, event_title, registered_at

## Notes
- Passwords are stored in plain text — for demo/college use only
- Must be opened through Tomcat, not as a local file
- PostgreSQL and the app machine must be on the same network