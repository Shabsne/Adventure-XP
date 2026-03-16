// ── LOGIN ────────────────────────────────────────────────
function showTab(tab) {
    document.getElementById('form-login').classList.toggle('hidden', tab !== 'login');
    document.getElementById('form-register').classList.toggle('hidden', tab !== 'register');
    document.getElementById('tab-login').classList.toggle('active', tab === 'login');
    document.getElementById('tab-register').classList.toggle('active', tab === 'register');
}

async function login(event) {
    if (event) event.preventDefault();
    const mail     = document.getElementById('mail')?.value;
    const password = document.getElementById('password')?.value;
    const btn      = document.getElementById('login-btn');
    const errBox   = document.getElementById('login-error') || document.getElementById('error');

    if (!mail || !password) return;

    btn.textContent = 'Logger ind...';
    btn.disabled    = true;

    try {
        const response = await fetch('/login', {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify({ mail, password })
        });

        if (response.ok) {
            window.location.href = '/dashboard';
        } else {
            errBox.classList.remove('hidden');
            btn.textContent = 'Log ind →';
            btn.disabled    = false;
        }
    } catch (e) {
        errBox.classList.remove('hidden');
        btn.textContent = 'Log ind →';
        btn.disabled    = false;
    }
}

// ── OPRET BRUGER ─────────────────────────────────────────
async function register() {
    const name     = document.getElementById('reg-name').value.trim();
    const mail     = document.getElementById('reg-mail').value.trim();
    const birth    = document.getElementById('reg-birth').value;
    const password = document.getElementById('reg-password').value;
    const btn      = document.getElementById('register-btn');
    const errBox   = document.getElementById('register-error');

    errBox.classList.add('hidden');

    if (!name || !mail || !birth || !password) {
        errBox.textContent = 'Udfyld alle felter';
        errBox.classList.remove('hidden');
        return;
    }

    btn.textContent = 'Opretter...';
    btn.disabled    = true;

    try {
        const response = await fetch('/register', {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify({ name, mail, birthDate: birth, password })
        });

        if (response.ok) {
            const loginRes = await fetch('/login', {
                method:  'POST',
                headers: { 'Content-Type': 'application/json' },
                body:    JSON.stringify({ mail, password })
            });
            if (loginRes.ok) {
                window.location.href = '/dashboard';
            } else {
                showTab('login');
            }
        } else {
            const msg = await response.text();
            errBox.textContent = msg || 'E-mail er allerede i brug';
            errBox.classList.remove('hidden');
        }
    } catch (e) {
        errBox.textContent = 'Serverfejl — prøv igen';
        errBox.classList.remove('hidden');
    } finally {
        btn.textContent = 'Opret konto →';
        btn.disabled    = false;
    }
}

// ── PROFIL SIDE ──────────────────────────────────────────
if (document.getElementById('profile-card')) {
    loadProfile();
}

let currentProfile = null;

async function loadProfile() {
    try {
        const res = await fetch('/me');
        if (!res.ok) {
            window.location.href = '/login.html';
            return;
        }
        currentProfile = await res.json();
        if (!currentProfile) {
            window.location.href = '/login.html';
            return;
        }
        renderProfile(currentProfile);
    } catch (e) {
        window.location.href = '/login.html';
    }
}

function renderProfile(profile) {
    const initials = profile.name
        ? profile.name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2)
        : '?';
    document.getElementById('avatar-initials').textContent  = initials;
    document.getElementById('view-name').textContent        = profile.name || '—';
    document.getElementById('view-name-field').textContent  = profile.name || '—';
    document.getElementById('view-mail').textContent        = profile.mail || '—';
    document.getElementById('view-role').textContent        = formatRole(profile.role);
    document.getElementById('view-role-field').textContent  = formatRole(profile.role);

    const birth = profile.birthDate
        ? new Date(profile.birthDate).toLocaleDateString('da-DK', {
            day: 'numeric', month: 'long', year: 'numeric'
        })
        : '—';
    document.getElementById('view-birth').textContent = birth;
    // Password vises altid maskeret — vi har ikke den rå værdi
    document.getElementById('view-password').textContent = '••••••••';
}

function formatRole(role) {
    const map = {
        KUNDE:            'Kunde',
        ADMIN:            'Administrator',
        SERVICE:          'Servicetekniker',
        SÆSONMEDARBEJDER: 'Sæsonmedarbejder'
    };
    return map[role] || role || '—';
}

// ── LOG UD ───────────────────────────────────────────────
async function logout() {
    await fetch('/logout', { method: 'POST' });
    window.location.href = '/login.html';
}

// ── TOAST ────────────────────────────────────────────────
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <div class="toast-icon">${type === 'success' ? '✓' : '✕'}</div>
        <div class="toast-text"><strong>${message}</strong></div>
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.classList.add('toast-visible'), 10);
    setTimeout(() => {
        toast.classList.remove('toast-visible');
        setTimeout(() => toast.remove(), 400);
    }, 3500);
}