// Mock Backend Server for Eventra
// Simulates the Java backend API endpoints for testing

const http = require('http');
const url = require('url');

// Mock data
let users = [
    { id: 1, username: 'admin', email: 'admin@eventra.com', password: 'admin123', fullName: 'System Administrator', phone: '1234567890', role: 'admin' },
    { id: 2, username: 'john_doe', email: 'john@example.com', password: 'password123', fullName: 'John Doe', phone: '9876543210', role: 'user' },
    { id: 3, username: 'jane_smith', email: 'jane@example.com', password: 'password123', fullName: 'Jane Smith', phone: '8765432109', role: 'organizer' }
];

let events = [
    { id: 1, title: 'Tech Symposium 2024', description: 'Annual technology symposium featuring latest innovations and workshops', category: 'tech', date: '2024-05-15', time: '09:00:00', location: 'Main Auditorium, Tech Campus', maxParticipants: 200, currentParticipants: 45, price: 50.00, imageUrl: 'https://picsum.photos/seed/tech2024/400/300.jpg', organizerId: 3, status: 'upcoming' },
    { id: 2, title: 'Basketball Tournament', description: 'Inter-college basketball championship', category: 'sports', date: '2024-06-20', time: '10:00:00', location: 'Sports Complex', maxParticipants: 100, currentParticipants: 78, price: 25.00, imageUrl: 'https://picsum.photos/seed/basketball2024/400/300.jpg', organizerId: 4, status: 'upcoming' },
    { id: 3, title: 'Cultural Festival', description: 'Annual cultural festival with music, dance, and drama performances', category: 'cultural', date: '2024-07-10', time: '18:00:00', location: 'Open Air Theater', maxParticipants: 500, currentParticipants: 234, price: 30.00, imageUrl: 'https://picsum.photos/seed/cultural2024/400/300.jpg', organizerId: 3, status: 'upcoming' },
    { id: 4, title: 'Hackathon 2024', description: '48-hour coding competition and innovation challenge', category: 'tech', date: '2024-08-05', time: '09:00:00', location: 'Innovation Lab', maxParticipants: 150, currentParticipants: 89, price: 75.00, imageUrl: 'https://picsum.photos/seed/hackathon2024/400/300.jpg', organizerId: 4, status: 'upcoming' },
    { id: 5, title: 'Music Concert', description: 'Live music concert featuring popular artists', category: 'cultural', date: '2024-09-12', time: '19:00:00', location: 'Concert Hall', maxParticipants: 1000, currentParticipants: 567, price: 100.00, imageUrl: 'https://picsum.photos/seed/concert2024/400/300.jpg', organizerId: 3, status: 'upcoming' }
];

let registrations = [
    { id: 1, eventId: 1, userId: 2, registrationDate: '2024-04-20', status: 'registered' },
    { id: 2, eventId: 2, userId: 2, registrationDate: '2024-04-21', status: 'registered' },
    { id: 3, eventId: 1, userId: 3, registrationDate: '2024-04-22', status: 'registered' }
];

let nextUserId = 4;
let nextEventId = 6;
let nextRegistrationId = 4;

const server = http.createServer((req, res) => {
    // Enable CORS
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
    
    if (req.method === 'OPTIONS') {
        res.writeHead(200);
        res.end();
        return;
    }

    const parsedUrl = url.parse(req.url, true);
    const path = parsedUrl.pathname;
    const method = req.method;

    console.log(`${method} ${path}`);

    // Parse request body for POST/PUT requests
    let body = '';
    req.on('data', chunk => {
        body += chunk.toString();
    });
    req.on('end', () => {
        const formData = new URLSearchParams(body);
        const data = {};
        formData.forEach((value, key) => {
            data[key] = value;
        });

        // Route handling
        if (path === '/api/test' && method === 'GET') {
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({
                success: true,
                message: 'Mock backend is working!',
                timestamp: new Date().toISOString()
            }));

        } else if (path === '/api/register' && method === 'POST') {
            const newUser = {
                id: nextUserId++,
                username: data.username,
                email: data.email,
                password: data.password,
                fullName: data.fullName,
                phone: data.phone,
                role: data.role || 'user'
            };
            users.push(newUser);
            res.writeHead(201, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({
                success: true,
                message: 'User registered successfully',
                user: { ...newUser, password: undefined }
            }));

        } else if (path === '/api/login' && method === 'POST') {
            const user = users.find(u => u.username === data.username && u.password === data.password);
            if (user) {
                res.writeHead(200, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: true,
                    message: 'Login successful',
                    user: { ...user, password: undefined }
                }));
            } else {
                res.writeHead(401, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: false,
                    message: 'Invalid username or password'
                }));
            }

        } else if (path === '/api/logout' && method === 'POST') {
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({
                success: true,
                message: 'Logout successful'
            }));

        } else if (path === '/api/events' && method === 'GET') {
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({
                success: true,
                message: 'Events retrieved successfully',
                events: events
            }));

        } else if (path.startsWith('/api/events/') && method === 'GET') {
            const eventId = parseInt(path.split('/')[3]);
            const event = events.find(e => e.id === eventId);
            if (event) {
                res.writeHead(200, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: true,
                    message: 'Event retrieved successfully',
                    event: event
                }));
            } else {
                res.writeHead(404, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: false,
                    message: 'Event not found'
                }));
            }

        } else if (path === '/api/events' && method === 'POST') {
            const newEvent = {
                id: nextEventId++,
                title: data.title,
                description: data.description,
                category: data.category,
                date: data.date,
                time: data.time,
                location: data.location,
                maxParticipants: parseInt(data.maxParticipants),
                currentParticipants: 0,
                price: parseFloat(data.price),
                imageUrl: data.imageUrl,
                organizerId: parseInt(data.organizerId),
                status: 'upcoming'
            };
            events.push(newEvent);
            res.writeHead(201, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({
                success: true,
                message: 'Event created successfully',
                event: newEvent
            }));

        } else if (path === '/api/event-registration' && method === 'POST') {
            const eventId = parseInt(data.eventId);
            const userId = parseInt(data.userId);
            
            // Check if already registered
            const existingRegistration = registrations.find(r => r.eventId === eventId && r.userId === userId);
            if (existingRegistration) {
                res.writeHead(400, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: false,
                    message: 'Already registered for this event'
                }));
                return;
            }

            // Add registration
            const newRegistration = {
                id: nextRegistrationId++,
                eventId: eventId,
                userId: userId,
                registrationDate: new Date().toISOString().split('T')[0],
                status: 'registered'
            };
            registrations.push(newRegistration);

            // Update event participant count
            const event = events.find(e => e.id === eventId);
            if (event) {
                event.currentParticipants++;
            }

            res.writeHead(201, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({
                success: true,
                message: 'Successfully registered for event'
            }));

        } else if (path.startsWith('/api/users/') && method === 'GET') {
            const userId = parseInt(path.split('/')[3]);
            const user = users.find(u => u.id === userId);
            if (user) {
                res.writeHead(200, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: true,
                    message: 'User retrieved successfully',
                    user: { ...user, password: undefined }
                }));
            } else {
                res.writeHead(404, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: false,
                    message: 'User not found'
                }));
            }

        } else if (path.startsWith('/api/users/') && method === 'PUT') {
            const userId = parseInt(path.split('/')[3]);
            const user = users.find(u => u.id === userId);
            if (user) {
                user.username = data.username || user.username;
                user.email = data.email || user.email;
                user.fullName = data.fullName || user.fullName;
                user.phone = data.phone || user.phone;
                
                res.writeHead(200, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: true,
                    message: 'User updated successfully',
                    user: { ...user, password: undefined }
                }));
            } else {
                res.writeHead(404, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: false,
                    message: 'User not found'
                }));
            }

        } else {
            res.writeHead(404, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({
                success: false,
                message: 'Endpoint not found'
            }));
        }
    });
});

const PORT = 8080;
server.listen(PORT, () => {
    console.log(`🚀 Mock Eventra Backend running at http://localhost:${PORT}`);
    console.log(`📡 API endpoints available:`);
    console.log(`   GET  /api/test - Test connection`);
    console.log(`   POST /api/register - Register user`);
    console.log(`   POST /api/login - User login`);
    console.log(`   POST /api/logout - User logout`);
    console.log(`   GET  /api/events - Get all events`);
    console.log(`   POST /api/events - Create event`);
    console.log(`   POST /api/event-registration - Register for event`);
    console.log(`   GET  /api/users/{id} - Get user profile`);
    console.log(`   PUT  /api/users/{id} - Update user profile`);
    console.log(`\n🎯 Frontend should connect to: http://localhost:${PORT}/api`);
    console.log(`⏹️  Press Ctrl+C to stop the server`);
});
