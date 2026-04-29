function getApiBaseUrl() {
    const match = window.location.pathname.match(/^\/([^/]+)\//);
    return match ? `/${match[1]}/api` : '/api';
}
let registrations = [];
let events = [];
let currentUser = null;

async function apiCall(endpoint, method = 'GET', data = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    };

    if (data && method !== 'GET') {
        options.body = new URLSearchParams(data);
    }

    const response = await fetch(`${getApiBaseUrl()}${endpoint}`, options);
    const result = await response.json();

    if (!response.ok || result.success === false) {
        throw new Error(result.message || 'Request failed');
    }

    return result;
}

function showRegistration(eventName) {
    const eventField = document.getElementById('event');
    if (eventField) {
        eventField.value = eventName;
    }

    if (currentUser) {
        document.getElementById('name').value = currentUser.fullName || currentUser.name || '';
        document.getElementById('email').value = currentUser.email || '';
        document.getElementById('phone').value = currentUser.phone || '';
    }

    showPage('register');
    window.location.hash = 'register';
    document.getElementById('name').focus();
}

document.getElementById('registrationForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();

    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const event = document.getElementById('event').value.trim();
    const phone = document.getElementById('phone').value.trim();

    try {
        const result = await apiCall('/event-registration', 'POST', {
            fullName: name,
            email,
            phone,
            eventTitle: event
        });

        alert('Registration successful! You have registered for: ' + event);
        this.reset();
        syncProfileFields();
        showPage('home');
        await loadRegistrations();
        await loadEvents();
        displayEvents();
    } catch (error) {
        alert('Registration error: ' + error.message);
    }
});

async function loadRegistrations() {
    try {
        const result = await apiCall('/registrations');
        registrations = result.data || [];
    } catch (error) {
        console.error('Failed to load registrations:', error);
    }
}

async function loadEvents() {
    try {
        const result = await apiCall('/events');
        events = result.data || [];
        return events;
    } catch (error) {
        console.error('Failed to load events:', error);
        return [];
    }
}

window.addEventListener('load', async function() {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
        currentUser = JSON.parse(storedUser);
    }

    syncProfileFields();
    await loadRegistrations();
    await loadEvents();
    updateEventCapacity();
    displayEvents();
    displayRegistrations();
    loadProfile();

    const initialSection = window.location.hash.replace('#', '');
    if (initialSection && document.getElementById(initialSection)) {
        showPage(initialSection);
    }
});

document.getElementById('searchInput').addEventListener('input', filterEvents);
document.getElementById('categoryFilter').addEventListener('change', filterEvents);
document.getElementById('dateFilter').addEventListener('change', filterEvents);

function filterEvents() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const categoryFilter = document.getElementById('categoryFilter').value;
    const dateFilter = document.getElementById('dateFilter').value;
    
    const filteredEvents = events.filter(event => {
        const matchesSearch = event.title.toLowerCase().includes(searchTerm);
        const matchesCategory = !categoryFilter || event.category === categoryFilter;
        const matchesDate = !dateFilter || (event.eventDate || event.date) === dateFilter;
        
        return matchesSearch && matchesCategory && matchesDate;
    });
    
    displayEvents(filteredEvents);
}

function displayEvents(eventsToShow = events) {
    const eventList = document.getElementById('eventList');
    
    if (eventsToShow.length === 0) {
        eventList.innerHTML = '<p>No events found.</p>';
        return;
    }
    
    let html = '';
    eventsToShow.forEach(event => {
        const capacity = getEventCapacity(event.title);
        const registered = typeof event.registered === 'number' ? event.registered : 0;
        const isFull = registered >= capacity;
        const buttonStatus = isFull ? 'disabled' : '';
        
        html += '<div class="event-card" data-category="' + event.category + '" data-date="' + (event.eventDate || event.date || '') + '" data-capacity="' + capacity + '">';
        html += '<h3>' + event.title + '</h3>';
        html += '<p>Date: ' + (event.eventDate || event.date || '') + '</p>';
        html += '<p>Location: ' + event.location + '</p>';
        html += '<p>' + event.description + '</p>';
        html += '<p class="capacity">Seats: <span class="current">' + registered + '</span>/' + capacity + '</p>';
        html += '<button onclick="showRegistration(\'' + event.title + '\')" ' + buttonStatus + '>Register</button>';
        html += '</div>';
    });
    
    eventList.innerHTML = html;
}

function updateEventCapacity() {
    events.forEach(event => {
        const eventRegistrations = registrations.filter(reg => (reg.eventTitle || reg.event) === event.title);
        event.registered = eventRegistrations.length;
    });
}

function getEventCapacity(title) {
    if (title === 'Tech Symposium') return 50;
    if (title === 'Basketball Tournament') return 30;
    if (title === 'Cultural Fest') return 100;
    return 0;
}

function displayRegistrations() {
    // This function can be used to display all registrations for admin view
    console.log('Total registrations:', registrations.length);
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

    window.location.hash = pageId;
    
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

document.getElementById('name').addEventListener('input', function() {
    if (this.value.length < 2) {
        this.style.borderColor = '#e74c3c';
    } else {
        this.style.borderColor = '#27ae60';
    }
});

document.getElementById('email').addEventListener('input', function() {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(this.value)) {
        this.style.borderColor = '#e74c3c';
    } else {
        this.style.borderColor = '#27ae60';
    }
});

document.getElementById('phone').addEventListener('input', function() {
    if (this.value.length < 10) {
        this.style.borderColor = 'red';
    } else {
        this.style.borderColor = 'green';
    }
});

// Admin Dashboard Functions
function refreshDashboard() {
    updateDashboardCounters();
    console.log('Dashboard refreshed');
}

function updateDashboardCounters() {
    const totalEvents = document.querySelectorAll('.event-card').length;
    const uniqueUsers = new Set(registrations.map(reg => reg.email)).size;
    const totalRegistrations = registrations.length;
    
    document.getElementById('adminTotalEvents').textContent = totalEvents;
    document.getElementById('adminTotalUsers').textContent = uniqueUsers;
    document.getElementById('adminTotalRegistrations').textContent = totalRegistrations;
}

function showRegistrations() {
    showPage('registrations');
    displayRegistrations();
}

function hideRegistrations() {
    showPage('admin');
}

function displayRegistrations(filteredRegistrations = null) {
    const registrationsList = document.getElementById('registrationsList');
    const registrationsToDisplay = filteredRegistrations || registrations;
    
    if (registrationsToDisplay.length === 0) {
        registrationsList.innerHTML = '<p>No registrations found.</p>';
        return;
    }
    
    registrationsList.innerHTML = registrationsToDisplay.map(registration => `
        <div class="registration-item">
            <div class="registration-info">
                <h4>${registration.fullName || registration.name}</h4>
                <p><strong>Email:</strong> ${registration.email}</p>
                <p><strong>Event:</strong> ${registration.eventTitle || registration.event}</p>
                <p><strong>Phone:</strong> ${registration.phone}</p>
                <p><strong>Date:</strong> ${registration.registeredAt || registration.date || ''}</p>
            </div>
        </div>
    `).join('');
}

function updateRegistrationStatus(registrationId, status) {
    const registration = registrations.find(reg => reg.id === registrationId);
    if (registration) {
        registration.status = status;
        localStorage.setItem('eventRegistrations', JSON.stringify(registrations));
        displayRegistrations();
        updateDashboardCounters();
        
        const action = status === 'approved' ? 'approved' : 'rejected';
        console.log(`Registration ${registrationId} ${action}`);
    }
}

function searchRegistrations() {
    const searchTerm = document.getElementById('registrationSearchInput').value.toLowerCase();
    
    if (searchTerm === '') {
        displayRegistrations();
        return;
    }
    
    const filteredRegistrations = registrations.filter(registration => 
        (registration.fullName || registration.name || '').toLowerCase().includes(searchTerm) ||
        registration.email.toLowerCase().includes(searchTerm) ||
        (registration.eventTitle || registration.event || '').toLowerCase().includes(searchTerm)
    );
    
    displayRegistrations(filteredRegistrations);
}

// Admin navigation - update dashboard when admin page is shown
const originalShowPage = showPage;
showPage = function(pageId) {
    originalShowPage(pageId);
    if (pageId === 'admin') {
        updateDashboardCounters();
    }
};

// Student 2 Features - My Registered Events
function filterUserEvents() {
    const userEmailInput = document.getElementById('userEmailFilter');
    const userEmail = userEmailInput.value.trim() || (currentUser && currentUser.email) || '';
    
    if (!userEmail) {
        alert('Please enter your email address');
        return;
    }

    userEmailInput.value = userEmail;
    
    const userRegistrations = registrations.filter(reg => 
        reg.email.toLowerCase() === userEmail.toLowerCase()
    );
    
    const userEventsList = document.getElementById('userEventsList');
    
    if (userRegistrations.length === 0) {
        userEventsList.innerHTML = '<p class="no-events">No events found for this email address</p>';
        return;
    }
    
    let eventsHTML = '';
    userRegistrations.forEach(registration => {
        eventsHTML += `
            <div class="user-event-card">
                <h4>${registration.eventTitle || registration.event}</h4>
                <p><strong>Name:</strong> ${registration.fullName || registration.name}</p>
                <p><strong>Email:</strong> ${registration.email}</p>
                <p><strong>Phone:</strong> ${registration.phone}</p>
                <p class="registration-date"><strong>Registered on:</strong> ${registration.registeredAt || registration.date || ''}</p>
            </div>
        `;
    });
    
    userEventsList.innerHTML = eventsHTML;
}

function syncProfileFields() {
    if (!currentUser) {
        return;
    }

    document.getElementById('profileName').value = currentUser.fullName || currentUser.name || '';
    document.getElementById('profileEmail').value = currentUser.email || '';
    document.getElementById('profilePhone').value = currentUser.phone || '';
    document.getElementById('userEmailFilter').value = currentUser.email || '';
}

async function saveProfile() {
    const profile = {
        name: document.getElementById('profileName').value,
        email: document.getElementById('profileEmail').value,
        phone: document.getElementById('profilePhone').value,
        department: document.getElementById('profileDepartment').value,
        year: document.getElementById('profileYear').value
    };
    
    if (!profile.email) {
        alert('Please enter your email address');
        return;
    }

    try {
        await apiCall('/profile', 'POST', {
            email: profile.email,
            fullName: profile.name,
            phone: profile.phone
        });

        currentUser = {
            ...(currentUser || {}),
            fullName: profile.name,
            name: profile.name,
            email: profile.email,
            phone: profile.phone,
            department: profile.department,
            year: profile.year
        };

        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        localStorage.setItem('userProfile', JSON.stringify(profile));
        alert('Profile saved successfully!');
        updateUserStats(profile.email);
    } catch (error) {
        alert('Profile save failed: ' + error.message);
    }
}

function loadProfile() {
    syncProfileFields();

    const storedProfile = localStorage.getItem('userProfile');
    if (storedProfile) {
        const profile = JSON.parse(storedProfile);
        document.getElementById('profileName').value = profile.name || document.getElementById('profileName').value;
        document.getElementById('profileEmail').value = profile.email || document.getElementById('profileEmail').value;
        document.getElementById('profilePhone').value = profile.phone || document.getElementById('profilePhone').value;
        document.getElementById('profileDepartment').value = profile.department || '';
        document.getElementById('profileYear').value = profile.year || '';
        
        if (profile.email) {
            updateUserStats(profile.email);
        }
    } else if (currentUser && currentUser.email) {
        updateUserStats(currentUser.email);
    }
}

function updateUserStats(userEmail) {
    const userRegistrations = registrations.filter(reg => 
        reg.email.toLowerCase() === userEmail.toLowerCase()
    );
    
    document.getElementById('totalEvents').textContent = userRegistrations.length;
    
    const today = new Date();
    const upcomingEvents = userRegistrations.filter(reg => {
        const eventDateValue = reg.registeredAt || reg.date;
        const eventDate = new Date(eventDateValue);
        return !Number.isNaN(eventDate.getTime()) && eventDate >= today;
    });
    
    document.getElementById('upcomingEvents').textContent = upcomingEvents.length;
}

function updateUserStats(userEmail) {
    const userRegistrations = registrations.filter(reg => 
        reg.email.toLowerCase() === userEmail.toLowerCase()
    );
    
    document.getElementById('totalEvents').textContent = userRegistrations.length;
    
    const today = new Date();
    const upcomingEvents = userRegistrations.filter(reg => {
        const matchedEvent = events.find(event => event.title === (reg.eventTitle || reg.event));
        const eventDate = new Date((matchedEvent && matchedEvent.eventDate) || reg.registeredAt || reg.date);
        return eventDate >= today;
    });
    
    document.getElementById('upcomingEvents').textContent = upcomingEvents.length;
}

window.addEventListener('load', function() {
    updateDashboardCounters();
    if (typeof loadProfile === 'function') {
        loadProfile();
    }
});
