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
