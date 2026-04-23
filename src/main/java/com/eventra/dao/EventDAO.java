package com.eventra.dao;

import com.eventra.model.Event;
import com.eventra.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    
    public boolean createEvent(Event event) throws SQLException {
        String sql = "INSERT INTO events (title, description, category, date, time, location, max_participants, current_participants, price, image_url, organizer_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getCategory());
            pstmt.setString(4, event.getDate());
            pstmt.setString(5, event.getTime());
            pstmt.setString(6, event.getLocation());
            pstmt.setInt(7, event.getMaxParticipants());
            pstmt.setInt(8, event.getCurrentParticipants());
            pstmt.setDouble(9, event.getPrice());
            pstmt.setString(10, event.getImageUrl());
            pstmt.setInt(11, event.getOrganizerId());
            pstmt.setString(12, event.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }
    
    public Event getEventById(int eventId) throws SQLException {
        String sql = "SELECT * FROM events WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, eventId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractEventFromResultSet(rs);
            }
        }
        return null;
    }
    
    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }
        }
        return events;
    }
    
    public List<Event> getEventsByCategory(String category) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE category = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }
        }
        return events;
    }
    
    public List<Event> getEventsByOrganizer(int organizerId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE organizer_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, organizerId);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }
        }
        return events;
    }
    
    public boolean updateEvent(Event event) throws SQLException {
        String sql = "UPDATE events SET title = ?, description = ?, category = ?, date = ?, time = ?, location = ?, max_participants = ?, price = ?, image_url = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getCategory());
            pstmt.setString(4, event.getDate());
            pstmt.setString(5, event.getTime());
            pstmt.setString(6, event.getLocation());
            pstmt.setInt(7, event.getMaxParticipants());
            pstmt.setDouble(8, event.getPrice());
            pstmt.setString(9, event.getImageUrl());
            pstmt.setString(10, event.getStatus());
            pstmt.setInt(11, event.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteEvent(int eventId) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, eventId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean registerForEvent(int eventId, int userId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if user is already registered
            String checkSql = "SELECT COUNT(*) FROM event_registrations WHERE event_id = ? AND user_id = ?";
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, eventId);
                checkPstmt.setInt(2, userId);
                ResultSet rs = checkPstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    conn.rollback();
                    return false; // Already registered
                }
            }
            
            // Add registration
            String insertSql = "INSERT INTO event_registrations (event_id, user_id, registration_date) VALUES (?, ?, NOW())";
            try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                insertPstmt.setInt(1, eventId);
                insertPstmt.setInt(2, userId);
                insertPstmt.executeUpdate();
            }
            
            // Update current participants count
            String updateSql = "UPDATE events SET current_participants = current_participants + 1 WHERE id = ?";
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setInt(1, eventId);
                updatePstmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
    
    private Event extractEventFromResultSet(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setCategory(rs.getString("category"));
        event.setDate(rs.getString("date"));
        event.setTime(rs.getString("time"));
        event.setLocation(rs.getString("location"));
        event.setMaxParticipants(rs.getInt("max_participants"));
        event.setCurrentParticipants(rs.getInt("current_participants"));
        event.setPrice(rs.getDouble("price"));
        event.setImageUrl(rs.getString("image_url"));
        event.setOrganizerId(rs.getInt("organizer_id"));
        event.setStatus(rs.getString("status"));
        event.setCreatedAt(rs.getTimestamp("created_at"));
        event.setUpdatedAt(rs.getTimestamp("updated_at"));
        return event;
    }
}
