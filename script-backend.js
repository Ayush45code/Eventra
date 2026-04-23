// Backend-connected JavaScript for Eventra
// This replaces the localStorage-only version with proper API calls

const API_BASE_URL = 'http://localhost:8080/api';

let currentUser = null;
let events = [];
let registrations = [];

// API Functions
async function apiCall(endpoint, method = 'GET', data = null) {
    try {
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        };
        
        if (data && method !== 'GET') {
            const formData = new URLSearchParams();
            Object.keys(data).forEach(key => {
                formData.append(key, data[key]);
            });
            options.body = formData;
        }
        
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        const result = await response.json();
        
        if (!response.ok) {
            throw new Error(result.message || 'API call failed');
        }
        
        return result;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Authentication Functions
async function login(username, password) {
    try {
        const result = await apiCall('/login', 'POST', { username, password });
        if (result.success) {
            currentUser = result.user;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            return { success: true, user: currentUser };
        }
        return { success: false, message: result.message };
    } catch (error) {
        return { success: false, message: error.message };
    }
}

async function register(userData) {
    try {
        const result = await apiCall('/register', 'POST', userData);
        return result;
    } catch (error) {
        return { success: false, message: error.message };
    }
}

async function logout() {
    try {
        await apiCall('/logout', 'POST');
        currentUser = null;
        localStorage.removeItem('currentUser');
        return { success: true };
    } catch (error) {
        return { success: false, message: error.message };
    }
}

// Event Functions
async function loadEvents() {
    try {
        const result = await apiCall('/events');
        if (result.success) {
            events = result.events;
            return events;
        }
        return [];
    } catch (error) {
        console.error('Failed to load events:', error);
        return [];
    }
}

async function createEvent(eventData) {
    try {
        const result = await apiCall('/events', 'POST', eventData);
        return result;
    } catch (error) {
        return { success: false, message: error.message };
    }
}

async function updateEvent(eventId, eventData) {
    try {
        const result = await apiCall(`/events/${eventId}`, 'PUT', eventData);
        return result;
    } catch (error) {
        return { success: false, message: error.message };
    }
}

async function deleteEvent(eventId) {
    try {
        const result = await apiCall(`/events/${eventId}`, 'DELETE');
        return result;
    } catch (error) {
        return { success: false, message: error.message };
    }
}

// Registration Functions
async function registerForEvent(eventId, userId) {
    try {
        const result = await apiCall('/event-registration', 'POST', { eventId, userId });
        return result;
    } catch (error) {
        return { success: false, message: error.message };
    }
}

// User Functions
async function loadUserProfile(userId) {
    try {
        const result = await apiCall(`/users/${userId}`);
        if (result.success) {
            return result.user;
        }
        return null;
    } catch (error) {
        console.error('Failed to load user profile:', error);
        return null;
    }
}

async function updateUserProfile(userId, userData) {
    try {
        const result = await apiCall(`/users/${userId}`, 'PUT', userData);
        return result;
    } catch (error) {
        return { success: false, message: error.message };
    }
}

// UI Functions
function showRegistration(eventName) {
    if (!currentUser) {
        alert('Please login first to register for events!');
        showPage('login');
        return;
    }
    showPage('register');
    document.getElementById('event').value = eventName;
    document.getElementById('name').focus();
}

// Registration Form Handler
document.getElementById('registrationForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    if (!currentUser) {
        alert('Please login first!');
        showPage('login');
        return;
    }
    
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const eventTitle = document.getElementById('event').value;
    const phone = document.getElementById('phone').value;
    
    // Find the event by title
    const event = events.find(e => e.title === eventTitle);
    if (!event) {
        alert('Event not found!');
        return;
    }
    
    try {
        const result = await registerForEvent(event.id, currentUser.id);
        if (result.success) {
            alert('Registration successful! You have registered for: ' + eventTitle);
            this.reset();
            showPage('home');
            await loadEvents(); // Refresh events
            displayEvents();
        } else {
            alert('Registration failed: ' + result.message);
        }
    } catch (error) {
        alert('Registration error: ' + error.message);
    }
});

// Login Form Handler
document.getElementById('loginForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        const result = await login(username, password);
        if (result.success) {
            alert('Login successful!');
            showPage('home');
            await loadEvents();
            displayEvents();
        } else {
            alert('Login failed: ' + result.message);
        }
    } catch (error) {
        alert('Login error: ' + error.message);
    }
});

// Register Form Handler
document.getElementById('registerForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const userData = {
        username: document.getElementById('registerUsername').value,
        email: document.getElementById('registerEmail').value,
        password: document.getElementById('registerPassword').value,
        fullName: document.getElementById('registerFullName').value,
        phone: document.getElementById('registerPhone').value,
        role: 'user'
    };
    
    try {
        const result = await register(userData);
        if (result.success) {
            alert('Registration successful! Please login.');
            showPage('login');
        } else {
            alert('Registration failed: ' + result.message);
        }
    } catch (error) {
        alert('Registration error: ' + error.message);
    }
});

// Event Display Functions
function displayEvents(eventsToShow = events) {
    const eventList = document.getElementById('eventList');
    
    if (eventsToShow.length === 0) {
        eventList.innerHTML = '<p>No events found.</p>';
        return;
    }
    
    let html = '';
    eventsToShow.forEach(event => {
        const isFull = event.currentParticipants >= event.maxParticipants;
        const buttonStatus = isFull ? 'disabled' : '';
        
        html += '<div class="event-card" data-category="' + event.category + '" data-date="' + event.date + '" data-capacity="' + event.maxParticipants + '">';
        html += '<h3>' + event.title + '</h3>';
        html += '<p>Date: ' + event.date + '</p>';
        html += '<p>Time: ' + event.time + '</p>';
        html += '<p>Location: ' + event.location + '</p>';
        html += '<p>' + event.description + '</p>';
        html += '<p class="capacity">Seats: <span class="current">' + event.currentParticipants + '</span>/' + event.maxParticipants + '</p>';
        html += '<p class="price">Price: $' + event.price + '</p>';
        html += '<button onclick="showRegistration(\'' + event.title + '\')" ' + buttonStatus + '>Register</button>';
        html += '</div>';
    });
    
    eventList.innerHTML = html;
}

function filterEvents() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const categoryFilter = document.getElementById('categoryFilter').value;
    const dateFilter = document.getElementById('dateFilter').value;
    
    const filteredEvents = events.filter(event => {
        const matchesSearch = event.title.toLowerCase().includes(searchTerm);
        const matchesCategory = !categoryFilter || event.category === categoryFilter;
        const matchesDate = !dateFilter || event.date === dateFilter;
        
        return matchesSearch && matchesCategory && matchesDate;
    });
    
    displayEvents(filteredEvents);
}

// Profile Functions
async function saveProfile() {
    if (!currentUser) {
        alert('Please login first!');
        return;
    }
    
    const profileData = {
        fullName: document.getElementById('profileName').value,
        email: document.getElementById('profileEmail').value,
        phone: document.getElementById('profilePhone').value
    };
    
    try {
        const result = await updateUserProfile(currentUser.id, profileData);
        if (result.success) {
            alert('Profile updated successfully!');
            // Update current user data
            currentUser = { ...currentUser, ...profileData };
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
        } else {
            alert('Profile update failed: ' + result.message);
        }
    } catch (error) {
        alert('Profile update error: ' + error.message);
    }
}

async function loadProfile() {
    if (!currentUser) return;
    
    try {
        const profile = await loadUserProfile(currentUser.id);
        if (profile) {
            document.getElementById('profileName').value = profile.fullName || '';
            document.getElementById('profileEmail').value = profile.email || '';
            document.getElementById('profilePhone').value = profile.phone || '';
        }
    } catch (error) {
        console.error('Failed to load profile:', error);
    }
}

// SPA Page Switching
function showPage(pageId) {
    // Hide all sections
    const sections = document.querySelectorAll('section');
    sections.forEach(section => {
        section.classList.remove('active');
    });
    
    // Show the selected section
    const selectedSection = document.getElementById(pageId);
    if (selectedSection) {
        selectedSection.classList.add('active');
    }
    
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
    
    // Load data for specific pages
    if (pageId === 'home') {
        loadEvents().then(() => displayEvents());
    } else if (pageId === 'profile') {
        loadProfile();
    }
}

// Initialize on page load
window.addEventListener('load', async function() {
    // Check if user is logged in
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
        currentUser = JSON.parse(storedUser);
    }
    
    // Load events
    await loadEvents();
    displayEvents();
    
    // Set up event listeners
    document.getElementById('searchInput').addEventListener('input', filterEvents);
    document.getElementById('categoryFilter').addEventListener('change', filterEvents);
    document.getElementById('dateFilter').addEventListener('change', filterEvents);
    
    // Form validation
    document.getElementById('name')?.addEventListener('input', function() {
        if (this.value.length < 2) {
            this.style.borderColor = '#e74c3c';
        } else {
            this.style.borderColor = '#27ae60';
        }
    });
    
    document.getElementById('email')?.addEventListener('input', function() {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(this.value)) {
            this.style.borderColor = '#e74c3c';
        } else {
            this.style.borderColor = '#27ae60';
        }
    });
    
    document.getElementById('phone')?.addEventListener('input', function() {
        if (this.value.length < 10) {
            this.style.borderColor = 'red';
        } else {
            this.style.borderColor = 'green';
        }
    });
});

// Admin Dashboard Functions
async function refreshDashboard() {
    if (!currentUser) {
        alert('Please login first to access admin dashboard!');
        showPage('login');
        return;
    }
    
    await loadEvents();
    updateDashboardCounters();
    console.log('Dashboard refreshed');
}

function updateDashboardCounters() {
    const totalEvents = events.length;
    const totalRegistrations = events.reduce((sum, event) => sum + event.currentParticipants, 0);
    
    document.getElementById('adminTotalEvents').textContent = totalEvents;
    document.getElementById('adminTotalRegistrations').textContent = totalRegistrations;
    // Note: Total users would require a separate API call
}

// Export functions for global access
window.showRegistration = showRegistration;
window.showPage = showPage;
window.saveProfile = saveProfile;
window.refreshDashboard = refreshDashboard;
