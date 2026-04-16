let registrations = [];

function showRegistration(eventName) {
    document.getElementById('register').scrollIntoView({ behavior: 'smooth' });
    document.getElementById('event').value = eventName;
    document.getElementById('name').focus();
}

document.getElementById('registrationForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const event = document.getElementById('event').value;
    const phone = document.getElementById('phone').value;
    
    const registration = {
        id: registrations.length + 1,
        name: name,
        email: email,
        event: event,
        phone: phone,
        date: new Date().toLocaleDateString()
    };
    
    registrations.push(registration);
    localStorage.setItem('eventRegistrations', JSON.stringify(registrations));
    
    alert('Registration successful! You have registered for: ' + event);
    this.reset();
    window.scrollTo({ top: 0, behavior: 'smooth' });
    console.log('New registration:', registration);
});

window.addEventListener('load', function() {
    const stored = localStorage.getItem('eventRegistrations');
    if (stored) {
        registrations = JSON.parse(stored);
    }
});

const homeLink = document.querySelector('a[href="#home"]');
const eventsLink = document.querySelector('a[href="#events"]');
const registerLink = document.querySelector('a[href="#register"]');

if (homeLink) {
    homeLink.addEventListener('click', function(e) {
        e.preventDefault();
        document.getElementById('home').scrollIntoView({ behavior: 'smooth' });
    });
}

if (eventsLink) {
    eventsLink.addEventListener('click', function(e) {
        e.preventDefault();
        document.getElementById('events').scrollIntoView({ behavior: 'smooth' });
    });
}

if (registerLink) {
    registerLink.addEventListener('click', function(e) {
        e.preventDefault();
        document.getElementById('register').scrollIntoView({ behavior: 'smooth' });
    });
}

document.getElementById('name').addEventListener('input', function() {
    if (this.value.length < 2) {
        this.style.borderColor = '#e74c3c';
    } else {
        this.style.borderColor = '#27ae60';
    }
});

document.getElementById('email').addEventListener('input', function() {
    let emailPattern;
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
    
    document.getElementById('totalEvents').textContent = totalEvents;
    document.getElementById('totalUsers').textContent = uniqueUsers;
    document.getElementById('totalRegistrations').textContent = totalRegistrations;
}

function showRegistrations() {
    document.getElementById('admin').classList.add('hidden');
    document.getElementById('registrations').classList.remove('hidden');
    displayRegistrations();
}

function hideRegistrations() {
    document.getElementById('registrations').classList.add('hidden');
    document.getElementById('admin').classList.remove('hidden');
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
                <h4>${registration.name}</h4>
                <p><strong>Email:</strong> ${registration.email}</p>
                <p><strong>Event:</strong> ${registration.event}</p>
                <p><strong>Phone:</strong> ${registration.phone}</p>
                <p><strong>Date:</strong> ${registration.date}</p>
                <span class="status-badge status-${registration.status || 'pending'}">${registration.status || 'pending'}</span>
            </div>
            <div class="registration-actions">
                ${!registration.status || registration.status === 'pending' ? `
                    <button class="approve-btn" onclick="updateRegistrationStatus(${registration.id}, 'approved')">Approve</button>
                    <button class="reject-btn" onclick="updateRegistrationStatus(${registration.id}, 'rejected')">Reject</button>
                ` : ''}
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
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    
    if (searchTerm === '') {
        displayRegistrations();
        return;
    }
    
    const filteredRegistrations = registrations.filter(registration => 
        registration.name.toLowerCase().includes(searchTerm) ||
        registration.email.toLowerCase().includes(searchTerm) ||
        registration.event.toLowerCase().includes(searchTerm)
    );
    
    displayRegistrations(filteredRegistrations);
}

// Admin navigation
const adminLink = document.querySelector('a[href="#admin"]');
if (adminLink) {
    adminLink.addEventListener('click', function(e) {
        e.preventDefault();
        document.getElementById('admin').scrollIntoView({ behavior: 'smooth' });
        updateDashboardCounters();
    });
}

// Initialize dashboard on page load
window.addEventListener('load', function() {
    const stored = localStorage.getItem('eventRegistrations');
    if (stored) {
        registrations = JSON.parse(stored);
    }
    updateDashboardCounters();
});
