package com.eventra.dao;

import com.eventra.model.Event;
import com.eventra.model.User;
import com.eventra.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ── REGISTER USER ──
    // username    = first name
    // full_name   = first + last
    // email       = email
    // password    = password
    // department  = department
    // roll_number = roll number
    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, full_name, email, password, department, roll_number) VALUES (?, ?, ?, ?, ?, ?)";

        String username = user.getUsername();
        if (username == null || username.trim().isEmpty()) {
            String email = user.getEmail() == null ? "" : user.getEmail().trim();
            int atIndex = email.indexOf('@');
            username = atIndex > 0 ? email.substring(0, atIndex) : email;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getDepartment());
            stmt.setString(6, user.getRollNumber());
            return stmt.executeUpdate() > 0;
        }
    }

    // ── LOGIN USER ──
    // Accepts username, email, or roll number
    public User loginUser(String loginValue, String password) throws SQLException {
        String sql = "SELECT id, username, full_name, email, password, department, roll_number, created_at " +
                     "FROM users WHERE (username = ? OR email = ? OR roll_number = ?) AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loginValue);
            stmt.setString(2, loginValue);
            stmt.setString(3, loginValue);
            stmt.setString(4, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        }
        return null;
    }

    // ── GET USER BY ID ──
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT id, username, full_name, email, password, department, roll_number, created_at FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        }
        return null;
    }

    // ── GET ALL USERS ──
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, full_name, email, password, department, roll_number, created_at FROM users ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) users.add(mapUser(rs));
        }
        return users;
    }

    // ── UPDATE PROFILE ──
 public boolean updateProfile(String email, String fullName, String phone) throws SQLException {
    if (fullName == null || fullName.trim().isEmpty()) return false;

    String sql = "UPDATE users SET full_name = ? WHERE email = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, fullName.trim());
        stmt.setString(2, email);
        return stmt.executeUpdate() > 0;
    }
}
    // ── GET ALL EVENTS ──
    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT id, title, category, event_date, location, description, created_at FROM events ORDER BY event_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) events.add(mapEvent(rs));
        }
        return events;
    }

    // ── GET EVENT BY ID ──
    public Event getEventById(int eventId) throws SQLException {
        String sql = "SELECT id, title, category, event_date, location, description, created_at FROM events WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapEvent(rs);
            }
        }
        return null;
    }

    // ── REGISTER FOR EVENT ──
    public boolean registerForEvent(String fullName, String email, String phone, String eventTitle) throws SQLException {
        String checkSql  = "SELECT COUNT(*) FROM registrations WHERE email = ? AND event_title = ?";
        String insertSql = "INSERT INTO registrations (full_name, email, phone, event_title) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, email);
                checkStmt.setString(2, eventTitle);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) return false;
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, fullName);
                insertStmt.setString(2, email);
                insertStmt.setString(3, phone);
                insertStmt.setString(4, eventTitle);
                return insertStmt.executeUpdate() > 0;
            }
        }
    }

    // ── GET REGISTRATIONS ──
    public List<RegistrationRecord> getRegistrations(String email) throws SQLException {
        List<RegistrationRecord> registrations = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT id, full_name, email, phone, event_title, registered_at FROM registrations"
        );

        boolean hasEmail = email != null && !email.trim().isEmpty();
        if (hasEmail) sql.append(" WHERE email = ?");
        sql.append(" ORDER BY registered_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            if (hasEmail) stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) registrations.add(mapRegistration(rs));
            }
        }
        return registrations;
    }

    // ── COUNT HELPERS ──
    public int countUsers()         throws SQLException { return countRows("SELECT COUNT(*) FROM users"); }
    public int countEvents()        throws SQLException { return countRows("SELECT COUNT(*) FROM events"); }
    public int countRegistrations() throws SQLException { return countRows("SELECT COUNT(*) FROM registrations"); }

    private int countRows(String sql) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // ── MAP USER FROM DB ROW ──
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setDepartment(rs.getString("department"));
        user.setRollNumber(rs.getString("roll_number"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }

    // ── MAP EVENT FROM DB ROW ──
    private Event mapEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setCategory(rs.getString("category"));
        event.setEventDate(rs.getString("event_date"));
        event.setLocation(rs.getString("location"));
        event.setDescription(rs.getString("description"));
        event.setCreatedAt(rs.getTimestamp("created_at"));
        return event;
    }

    // ── MAP REGISTRATION FROM DB ROW ──
    private RegistrationRecord mapRegistration(ResultSet rs) throws SQLException {
        return new RegistrationRecord(
            rs.getInt("id"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("event_title"),
            rs.getTimestamp("registered_at")
        );
    }

    // ── REGISTRATION RECORD CLASS ──
    public static class RegistrationRecord {
        private final int id;
        private final String fullName;
        private final String email;
        private final String phone;
        private final String eventTitle;
        private final java.sql.Timestamp registeredAt;

        public RegistrationRecord(int id, String fullName, String email, String phone, String eventTitle, java.sql.Timestamp registeredAt) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.eventTitle = eventTitle;
            this.registeredAt = registeredAt;
        }

        public int getId()                          { return id; }
        public String getFullName()                 { return fullName; }
        public String getEmail()                    { return email; }
        public String getPhone()                    { return phone; }
        public String getEventTitle()               { return eventTitle; }
        public java.sql.Timestamp getRegisteredAt() { return registeredAt; }
    }
}