# Eventra Backend - Apache Tomcat + JDBC

This is the backend implementation for the Eventra event management system using Apache Tomcat and JDBC for database connectivity.

## Prerequisites

- Java 11 or higher
- Apache Tomcat 9.0 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher

## Database Setup

1. Install MySQL Server on your system
2. Create the database and tables using the provided schema:

```bash
mysql -u root -p < database_schema.sql
```

3. Update database credentials in `src/main/java/com/eventra/util/DatabaseConnection.java`:
   - URL: `jdbc:mysql://localhost:3306/eventra_db`
   - Username: Your MySQL username (default: `root`)
   - Password: Your MySQL password

## Project Setup

1. Clone the repository or copy the project files
2. Navigate to the project directory
3. Build the project using Maven:

```bash
mvn clean install
```

4. This will create a `eventra.war` file in the `target/` directory

## Deployment

### Option 1: Deploy to Tomcat manually

1. Copy `target/eventra.war` to Tomcat's `webapps/` directory
2. Start Tomcat server
3. The application will be available at `http://localhost:8080/eventra/`

### Option 2: Use Maven Tomcat Plugin

Add the following to your `pom.xml` if you want to use the Maven Tomcat plugin:

```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <path>/eventra</path>
        <port>8080</port>
    </configuration>
</plugin>
```

Then run:

```bash
mvn tomcat7:deploy
```

## API Endpoints

### Authentication

- `POST /api/register` - Register a new user
  - Parameters: username, email, password, fullName, phone, role (optional)
  
- `POST /api/login` - User login
  - Parameters: username, password
  
- `POST /api/logout` - User logout

### Events

- `GET /api/events` - Get all events
- `GET /api/events/{id}` - Get event by ID
- `GET /api/events/category/{category}` - Get events by category
- `POST /api/events` - Create a new event
  - Parameters: title, description, category, date, time, location, maxParticipants, price, imageUrl, organizerId
- `PUT /api/events/{id}` - Update an event
- `DELETE /api/events/{id}` - Delete an event

### Event Registration

- `POST /api/event-registration` - Register for an event
  - Parameters: eventId, userId

### Users

- `GET /api/users` - Get all users (admin only)
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user profile
  - Parameters: username, email, fullName, phone
- `DELETE /api/users/{id}` - Delete a user

## Database Schema

The application uses the following main tables:

- **users** - User information and authentication
- **events** - Event details and management
- **event_registrations** - User event registrations
- **event_feedback** - Event ratings and feedback
- **notifications** - User notifications

## Sample Data

The database schema includes sample data for testing:
- 4 sample users (admin, regular users, organizers)
- 5 sample events (tech symposium, basketball tournament, cultural festival, etc.)
- Sample registrations and feedback

## Configuration

### Database Connection

Update the database connection settings in `DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/eventra_db";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

### Tomcat Configuration

The `web.xml` file includes:
- UTF-8 character encoding
- CORS filter for development
- Session management
- Error page mappings

## Security Notes

- Passwords are stored in plain text for demo purposes. In production, use password hashing (BCrypt, etc.)
- CORS is enabled for all origins in development. Restrict in production
- Session timeout is set to 30 minutes
- Input validation is implemented but can be enhanced

## Testing

You can test the API endpoints using:

1. Postman or similar API testing tool
2. curl commands
3. The frontend HTML pages provided

Example curl commands:

```bash
# Register a user
curl -X POST http://localhost:8080/eventra/api/register \
  -d "username=testuser&email=test@example.com&password=password123&fullName=Test User&phone=1234567890"

# Login
curl -X POST http://localhost:8080/eventra/api/login \
  -d "username=testuser&password=password123"

# Get all events
curl -X GET http://localhost:8080/eventra/api/events
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── eventra/
│   │           ├── model/      # Data models (User, Event)
│   │           ├── dao/        # Data Access Objects
│   │           ├── servlet/    # HTTP Servlets
│   │           └── util/       # Utility classes
│   └── webapp/
│       └── WEB-INF/
│           └── web.xml        # Deployment descriptor
└── test/
    └── java/                   # Unit tests (if any)
```

## Troubleshooting

1. **Database Connection Error**: Check MySQL server is running and credentials are correct
2. **404 Errors**: Verify the WAR file is deployed correctly and context path is correct
3. **Compilation Errors**: Ensure Java 11+ and Maven dependencies are properly installed
4. **CORS Issues**: Check browser console and adjust CORS filter configuration

## Next Steps

1. Implement password hashing for security
2. Add input validation and sanitization
3. Implement role-based access control
4. Add unit and integration tests
5. Set up proper logging
6. Configure production-ready database connection pooling
