let registrations = [];
let events = [
    {
        id: 1,
        title: "Tech Symposium",
        category: "tech",
        date: "2024-12-15",
        location: "Main Auditorium",
        description: "Join us for a day of technology presentations and workshops.",
        capacity: 50,
        registered: 0
    },
    {
        id: 2,
        title: "Basketball Tournament",
        category: "sports",
        date: "2024-12-20",
        location: "Sports Complex",
        description: "Inter-department basketball championship.",
        capacity: 30,
        registered: 0
    },
    {
        id: 3,
        title: "Cultural Fest",
        category: "cultural",
        date: "2024-12-25",
        location: "Open Ground",
        description: "Annual cultural festival with music and dance performances.",
        capacity: 100,
        registered: 0
    }
];

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
    
    const storedEvents = localStorage.getItem('events');
    if (storedEvents) {
        events = JSON.parse(storedEvents);
    }
    
    updateEventCapacity();
    displayEvents();
    displayRegistrations();
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
        const matchesDate = !dateFilter || event.date === dateFilter;
        
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
        const isFull = event.registered >= event.capacity;
        const buttonStatus = isFull ? 'disabled' : '';
        
        html += '<div class="event-card" data-category="' + event.category + '" data-date="' + event.date + '" data-capacity="' + event.capacity + '">';
        html += '<h3>' + event.title + '</h3>';
        html += '<p>Date: ' + event.date + '</p>';
        html += '<p>Location: ' + event.location + '</p>';
        html += '<p>' + event.description + '</p>';
        html += '<p class="capacity">Seats: <span class="current">' + event.registered + '</span>/' + event.capacity + '</p>';
        html += '<button onclick="showRegistration(\'' + event.title + '\')" ' + buttonStatus + '>Register</button>';
        html += '</div>';
    });
    
    eventList.innerHTML = html;
}

function updateEventCapacity() {
    events.forEach(event => {
        const eventRegistrations = registrations.filter(reg => reg.event === event.title);
        event.registered = eventRegistrations.length;
    });
}

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
