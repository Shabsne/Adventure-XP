// ── LOGIN ────────────────────────────────────────────────

// Skift mellem login og opret-bruger fanerne
function showTab(tab) {
    document.getElementById('form-login').classList.toggle('hidden', tab !== 'login');
    document.getElementById('form-register').classList.toggle('hidden', tab !== 'register');
    document.getElementById('tab-login').classList.toggle('active', tab === 'login');
    document.getElementById('tab-register').classList.toggle('active', tab === 'register');
}
async function login() {
    const mail     = document.getElementById('mail')?.value;
    const password = document.getElementById('password')?.value;
    const btn      = document.getElementById('login-btn');
    const errBox   = document.getElementById('login-error');

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
// Kræver at din ven tilføjer: POST /register i ProfileController
async function register() {
    const name      = document.getElementById('reg-name').value.trim();
    const mail      = document.getElementById('reg-mail').value.trim();
    const birth     = document.getElementById('reg-birth').value;
    const password  = document.getElementById('reg-password').value;
    const btn       = document.getElementById('register-btn');
    const errBox    = document.getElementById('register-error');

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
            // Log automatisk ind efter oprettelse
            const loginRes = await fetch('/login', {
                method:  'POST',
                headers: { 'Content-Type': 'application/json' },
                body:    JSON.stringify({ mail, password })
            });
            if (loginRes.ok) {
                window.location.href = '/dashboard';
            } else {
                // Oprettet men login fejlede — send til login-siden
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


// Kører kun på profile.html — kalder loadProfile direkte
// (scriptet ligger i bunden af body, så DOMContentLoaded er allerede affyret)
if (document.getElementById('profile-card')) {
    loadProfile();
}

let currentProfile = null;

async function loadProfile() {
    try {
        const res = await fetch('/me');
        if (!res.ok || res.status === 204) {
            // Ikke logget ind — send til login
            window.location.href = '/login';
            return;
        }
        currentProfile = await res.json();
        renderProfile(currentProfile);
    } catch (e) {
        window.location.href = '/login';
    }
}

// ── VIS PROFIL ───────────────────────────────────────────
function renderProfile(profile) {
    // Avatar: initialer fra navn
    const initials = profile.name
        ? profile.name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2)
        : '?';
    document.getElementById('avatar-initials').textContent = initials;

    document.getElementById('view-name').textContent  = profile.name  || '—';
    document.getElementById('view-mail').textContent  = profile.mail  || '—';
    document.getElementById('view-role').textContent  = formatRole(profile.role);

    const birth = profile.birthDate
        ? new Date(profile.birthDate).toLocaleDateString('da-DK', {
            day: 'numeric', month: 'long', year: 'numeric'
        })
        : '—';
    document.getElementById('view-birth').textContent = birth;
}

function formatRole(role) {
    const map = {
        KUNDE:            'Kunde',
        ADMIN:            'Administrator',
        SERVICE:          'Servicetekniker',
        SÆSONMEDARBEJDER: 'Sæsonmedarbejder'
    };
    return map[role] || role || '';
}

// ── REDIGER TILSTAND ─────────────────────────────────────
function enterEditMode() {
    document.getElementById('view-mode').classList.add('hidden');
    document.getElementById('edit-mode').classList.remove('hidden');

    // Udfyld felter med eksisterende værdier
    document.getElementById('edit-name').value  = currentProfile.name  || '';
    document.getElementById('edit-mail').value  = currentProfile.mail  || '';
    document.getElementById('edit-birth').value = currentProfile.birthDate
        ? currentProfile.birthDate.slice(0, 10)
        : '';
    document.getElementById('edit-password').value = '';
}

function exitEditMode() {
    document.getElementById('edit-mode').classList.add('hidden');
    document.getElementById('view-mode').classList.remove('hidden');
}

// ── GEM ÆNDRINGER ────────────────────────────────────────
// Kræver at din ven tilføjer: PUT /me i ProfileController
async function saveProfile() {
    const btn = document.getElementById('save-btn');
    btn.textContent = 'Gemmer...';
    btn.disabled    = true;

    const updated = {
        name:      document.getElementById('edit-name').value,
        mail:      document.getElementById('edit-mail').value,
        birthDate: document.getElementById('edit-birth').value || null,
    };

    // Tilføj kun password hvis det er udfyldt
    const newPassword = document.getElementById('edit-password').value;
    if (newPassword) updated.password = newPassword;

    try {
        const res = await fetch('/me', {
            method:  'PUT',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(updated)
        });

        if (res.ok) {
            currentProfile = await res.json();
            renderProfile(currentProfile);
            exitEditMode();
            showToast('Oplysninger opdateret ✓', 'success');
        } else {
            showToast('Kunne ikke gemme — prøv igen', 'error');
        }
    } catch (e) {
        showToast('Serverfejl — prøv igen', 'error');
    } finally {
        btn.textContent = 'Gem ændringer';
        btn.disabled    = false;
    }
}

// ── LOG UD ───────────────────────────────────────────────
async function logout() {
    await fetch('/logout', { method: 'POST' });
    window.location.href = '/login';
}

// ── SLET KONTO ───────────────────────────────────────────
// Kræver at din ven tilføjer: DELETE /me i ProfileController
function openDeleteModal() {
    document.getElementById('modal-overlay').classList.remove('hidden');
    document.getElementById('modal-overlay').classList.add('open');
}

function closeDeleteModal() {
    document.getElementById('modal-overlay').classList.remove('open');
    setTimeout(() => {
        document.getElementById('modal-overlay').classList.add('hidden');
    }, 300);
}

async function confirmDelete() {
    try {
        const res = await fetch('/me', { method: 'DELETE' });
        if (res.ok) {
            await fetch('/logout', { method: 'POST' });
            window.location.href = '/login';
        } else {
            showToast('Kunne ikke slette konto', 'error');
        }
    } catch (e) {
        showToast('Serverfejl — prøv igen', 'error');
    }
    closeDeleteModal();
}

// ── TOAST ────────────────────────────────────────────────
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <div class="toast-icon">${type === 'success' ? '✓' : '✕'}</div>
        <div class="toast-text">
            <strong>${message}</strong>
        </div>
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.classList.add('toast-visible'), 10);
    setTimeout(() => {
        toast.classList.remove('toast-visible');
        setTimeout(() => toast.remove(), 400);
    }, 3500);
}