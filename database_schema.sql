-- Eventra simple schema
-- Designed to stay easy to read and easy to demo

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    category VARCHAR(50) NOT NULL,
    event_date DATE NOT NULL,
    location VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS registrations (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL,
    phone VARCHAR(20),
    event_title VARCHAR(120) NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (email, event_title)
);

INSERT INTO events (title, category, event_date, location, description) VALUES
('Tech Symposium', 'tech', '2026-05-15', 'Main Auditorium', 'Talks, demos, and workshops for students.'),
('Basketball Tournament', 'sports', '2026-05-22', 'Sports Complex', 'Simple inter-department basketball event.'),
('Cultural Fest', 'cultural', '2026-05-29', 'Open Ground', 'Music, dance, and performances.'),
('Hackathon', 'tech', '2026-06-05', 'Innovation Lab', 'A short coding challenge for teams.'),
('Freshers Day', 'cultural', '2026-06-12', 'College Auditorium', 'Welcome event for new students.');

INSERT INTO users (username, full_name, email, password, phone) VALUES
('admin', 'System Admin', 'admin@eventra.com', 'admin123', '9999999999'),
('john', 'John Doe', 'john@example.com', 'password123', '9876543210');
