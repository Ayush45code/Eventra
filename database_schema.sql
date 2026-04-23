-- Eventra Database Schema
-- MySQL Database Schema for Event Management System

-- Create database
CREATE DATABASE IF NOT EXISTS eventra_db;
USE eventra_db;

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('user', 'admin', 'organizer') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Events table
CREATE TABLE events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    location VARCHAR(255) NOT NULL,
    max_participants INT NOT NULL DEFAULT 100,
    current_participants INT DEFAULT 0,
    price DECIMAL(10,2) DEFAULT 0.00,
    image_url VARCHAR(500),
    organizer_id INT NOT NULL,
    status ENUM('upcoming', 'ongoing', 'completed', 'cancelled') DEFAULT 'upcoming',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_date (date),
    INDEX idx_organizer (organizer_id)
);

-- Event registrations table
CREATE TABLE event_registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('registered', 'attended', 'cancelled') DEFAULT 'registered',
    UNIQUE KEY unique_registration (event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_event (event_id),
    INDEX idx_user (user_id)
);

-- Event feedback table
CREATE TABLE event_feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    feedback_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_feedback (event_id, user_id),
    INDEX idx_event_feedback (event_id),
    INDEX idx_user_feedback (user_id)
);

-- Notifications table
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('info', 'warning', 'success', 'error') DEFAULT 'info',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_notifications (user_id),
    INDEX idx_read_status (is_read)
);

-- Insert sample data

-- Insert sample users
INSERT INTO users (username, email, password, full_name, phone, role) VALUES
('admin', 'admin@eventra.com', 'admin123', 'System Administrator', '1234567890', 'admin'),
('john_doe', 'john@example.com', 'password123', 'John Doe', '9876543210', 'user'),
('jane_smith', 'jane@example.com', 'password123', 'Jane Smith', '8765432109', 'organizer'),
('event_organizer', 'organizer@eventra.com', 'password123', 'Event Organizer', '7654321098', 'organizer');

-- Insert sample events
INSERT INTO events (title, description, category, date, time, location, max_participants, price, image_url, organizer_id) VALUES
('Tech Symposium 2024', 'Annual technology symposium featuring latest innovations and workshops', 'tech', '2024-05-15', '09:00:00', 'Main Auditorium, Tech Campus', 200, 50.00, 'https://picsum.photos/seed/tech2024/400/300.jpg', 3),
('Basketball Tournament', 'Inter-college basketball championship', 'sports', '2024-06-20', '10:00:00', 'Sports Complex', 100, 25.00, 'https://picsum.photos/seed/basketball2024/400/300.jpg', 4),
('Cultural Festival', 'Annual cultural festival with music, dance, and drama performances', 'cultural', '2024-07-10', '18:00:00', 'Open Air Theater', 500, 30.00, 'https://picsum.photos/seed/cultural2024/400/300.jpg', 3),
('Hackathon 2024', '48-hour coding competition and innovation challenge', 'tech', '2024-08-05', '09:00:00', 'Innovation Lab', 150, 75.00, 'https://picsum.photos/seed/hackathon2024/400/300.jpg', 4),
('Music Concert', 'Live music concert featuring popular artists', 'cultural', '2024-09-12', '19:00:00', 'Concert Hall', 1000, 100.00, 'https://picsum.photos/seed/concert2024/400/300.jpg', 3);

-- Insert sample event registrations
INSERT INTO event_registrations (event_id, user_id) VALUES
(1, 2), -- John Doe registered for Tech Symposium
(2, 2), -- John Doe registered for Basketball Tournament
(3, 2), -- John Doe registered for Cultural Festival
(1, 3), -- Jane Smith registered for Tech Symposium
(4, 3); -- Jane Smith registered for Hackathon

-- Insert sample feedback
INSERT INTO event_feedback (event_id, user_id, rating, comment) VALUES
(1, 2, 5, 'Excellent event! Very well organized and informative.'),
(2, 2, 4, 'Great tournament. Good competition and facilities.'),
(1, 3, 4, 'Good content and speakers. Would like more hands-on workshops.');

-- Insert sample notifications
INSERT INTO notifications (user_id, title, message, type) VALUES
(2, 'Event Reminder', 'Tech Symposium 2024 is starting tomorrow at 9:00 AM', 'info'),
(2, 'Registration Confirmation', 'You have successfully registered for Basketball Tournament', 'success'),
(3, 'Event Update', 'Hackathon 2024 schedule has been updated', 'warning'),
(1, 'Welcome', 'Welcome to Eventra! Your account has been created successfully.', 'success');

-- Create views for common queries

-- View for events with organizer details
CREATE VIEW event_details AS
SELECT 
    e.id,
    e.title,
    e.description,
    e.category,
    e.date,
    e.time,
    e.location,
    e.max_participants,
    e.current_participants,
    e.price,
    e.image_url,
    e.status,
    u.username as organizer_username,
    u.full_name as organizer_name,
    e.created_at
FROM events e
JOIN users u ON e.organizer_id = u.id;

-- View for user event registrations
CREATE VIEW user_registrations AS
SELECT 
    er.id as registration_id,
    er.event_id,
    er.user_id,
    er.registration_date,
    er.status,
    e.title as event_title,
    e.category,
    e.date as event_date,
    e.time as event_time,
    e.location,
    e.price,
    u.username as user_username
FROM event_registrations er
JOIN events e ON er.event_id = e.id
JOIN users u ON er.user_id = u.id;

-- View for event statistics
CREATE VIEW event_statistics AS
SELECT 
    e.id,
    e.title,
    e.category,
    e.max_participants,
    e.current_participants,
    (e.max_participants - e.current_participants) as available_spots,
    ROUND((e.current_participants * 100.0 / e.max_participants), 2) as fill_percentage,
    COUNT(er.id) as total_registrations,
    AVG(ef.rating) as average_rating,
    COUNT(ef.id) as feedback_count
FROM events e
LEFT JOIN event_registrations er ON e.id = er.event_id
LEFT JOIN event_feedback ef ON e.id = ef.event_id
GROUP BY e.id, e.title, e.category, e.max_participants, e.current_participants;
