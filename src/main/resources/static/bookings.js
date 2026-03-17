// ── STATE ────────────────────────────────────────────────
let allBookings  = [];
let activeFilter = 'ALL';

// ── INIT ─────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {
    // Auth-tjek: send ikke-loggede brugere til login
    try {
        const res = await fetch('/me');
        if (!res.ok) { window.location.href = '/login.html'; return; }
        const user = await res.json();
        if (!user) { window.location.href = '/login.html'; return; }
    } catch {
        window.location.href = '/login.html';
        return;
    }

    await fetchBookings();
});

// ── HENT BOOKINGER ───────────────────────────────────────
async function fetchBookings() {
    try {
        const res  = await fetch('/bookings');
        if (!res.ok) throw new Error('Server fejl');
        allBookings = await res.json();
    } catch {
        showToast('Kunne ikke hente bookinger', 'error');
        allBookings = [];
    }

    renderStats(allBookings);
    renderList(allBookings, activeFilter);
}

// ── STATS ────────────────────────────────────────────────
function renderStats(bookings) {
    const count = (status) => bookings.filter(b => b.status === status).length;

    document.getElementById('stat-total').textContent   = bookings.length;
    document.getElementById('stat-active').textContent  = count('ACTIVE');
    document.getElementById('stat-done').textContent    = count('COMPLETED');
    document.getElementById('stat-noshow').textContent  = count('NO_SHOW');
}

// ── FILTER ───────────────────────────────────────────────
function applyFilter(btn) {
    document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    activeFilter = btn.dataset.filter;
    renderList(allBookings, activeFilter);
}

// ── RENDER LISTE ─────────────────────────────────────────
function renderList(bookings, filter) {
    const list = document.getElementById('bookings-list');

    const filtered = filter === 'ALL'
        ? bookings
        : bookings.filter(b => b.status === filter);

    if (filtered.length === 0) {
        list.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">📭</div>
                <h3>Ingen bookinger fundet</h3>
                <p>Der er ingen bookinger der matcher dette filter.</p>
                <a href="/activity.html" class="btn-action" style="display:inline-block;width:auto;padding:13px 28px;text-decoration:none">
                    Book en aktivitet →
                </a>
            </div>`;
        return;
    }

    // Sorter: nyeste startTime øverst
    const sorted = [...filtered].sort(
        (a, b) => new Date(b.startTime) - new Date(a.startTime)
    );

    list.innerHTML = sorted.map((b, i) => bookingRowHTML(b, i)).join('');
}

// ── BOOKING ROW HTML ─────────────────────────────────────
function bookingRowHTML(b, index) {
    const start    = new Date(b.startTime);
    const end      = new Date(b.endTime);
    const day      = start.getDate();
    const month    = start.toLocaleDateString('da-DK', { month: 'short' }).toUpperCase();
    const timeStr  = `${fmt(start.getHours())}:${fmt(start.getMinutes())} – ${fmt(end.getHours())}:${fmt(end.getMinutes())}`;
    const dateStr  = start.toLocaleDateString('da-DK', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });

    const actName  = b.activity?.name  ?? 'Ukendt aktivitet';
    const userName = b.profile?.name   ?? 'Ukendt bruger';
    const parts    = b.participants ?? 0;

    const statusLabel = {
        ACTIVE:    'Aktiv',
        COMPLETED: 'Gennemført',
        NO_SHOW:   'Udeblivelse',
        CANCELLED: 'Aflyst',
    }[b.status] ?? b.status;

    const exclusiveHTML = b.exclusive
        ? `<span class="exclusive-tag">⭐ ${b.groupName ?? 'Eksklusiv'}</span>`
        : '';

    // Vis handlingsknapper kun for aktive bookinger
    const actionsHTML = b.status === 'ACTIVE' ? `
        <div class="booking-actions">
            <button class="action-btn" onclick="checkIn(${b.id})" title="Marker som mødt op">✓ Check ind</button>
            <button class="action-btn danger" onclick="noShow(${b.id})" title="Marker som udeblivelse">✕ Udeblivelse</button>
        </div>` : '<div class="booking-actions"></div>';

    return `
        <div class="booking-row" style="animation-delay:${index * 0.05}s" id="row-${b.id}">
            <div class="booking-date-block">
                <span class="booking-day">${day}</span>
                <span class="booking-month">${month}</span>
            </div>

            <div class="booking-info">
                <div class="booking-activity-name">${actName}</div>
                <div class="booking-meta">
                    <span>👤 ${userName}</span>
                    <span class="dot"></span>
                    <span>🕐 ${timeStr}</span>
                    <span class="dot"></span>
                    <span>👥 ${parts} pers.</span>
                    ${exclusiveHTML}
                </div>
            </div>

            <span class="status-badge status-${b.status}">${statusLabel}</span>

            ${actionsHTML}
        </div>`;
}

// ── CHECK IND ────────────────────────────────────────────
async function checkIn(id) {
    try {
        const res = await fetch(`/bookings/${id}/checkin`, { method: 'PUT' });
        if (!res.ok) throw new Error();
        showToast('Booking markeret som gennemført ✓', 'success');
        await fetchBookings();
    } catch {
        showToast('Kunne ikke opdatere booking', 'error');
    }
}

// ── NO SHOW ──────────────────────────────────────────────
async function noShow(id) {
    try {
        const res = await fetch(`/bookings/${id}/noshow`, { method: 'PUT' });
        if (!res.ok) throw new Error();
        showToast('Booking markeret som udeblivelse', 'success');
        await fetchBookings();
    } catch {
        showToast('Kunne ikke opdatere booking', 'error');
    }
}

// ── HJÆLPEFUNKTIONER ─────────────────────────────────────
function fmt(n) { return String(n).padStart(2, '0'); }

// ── TOAST ────────────────────────────────────────────────
function showToast(msg, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <div class="toast-icon">${type === 'success' ? '✓' : '✕'}</div>
        <div class="toast-text"><strong>${msg}</strong></div>`;
    document.body.appendChild(toast);
    setTimeout(() => toast.classList.add('toast-visible'), 10);
    setTimeout(() => {
        toast.classList.remove('toast-visible');
        setTimeout(() => toast.remove(), 400);
    }, 3500);
}
