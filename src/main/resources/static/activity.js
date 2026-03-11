// ── BILLEDE-MATCHING: nøgleord → Unsplash foto ──────────
const IMAGE_MAP = [
    { keys: ['kajak', 'kayak'],                        url: 'https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=600&q=80' },
    { keys: ['surf', 'surfing', 'bølge'],               url: 'https://images.unsplash.com/photo-1502680390469-be75c86b636f?w=600&q=80' },
    { keys: ['dyk', 'dykning', 'snorkel'],              url: 'https://images.unsplash.com/photo-1682687220067-dced9a881b56?w=600&q=80' },
    { keys: ['rafting', 'flod', 'strøm'],               url: 'https://images.unsplash.com/photo-1526401485004-46910ecc8e51?w=600&q=80' },
    { keys: ['sejl', 'båd', 'sejlads'],                 url: 'https://images.unsplash.com/photo-1500514966906-fe245eea9344?w=600&q=80' },
    { keys: ['klatr', 'klatring', 'bouldring'],         url: 'https://images.unsplash.com/photo-1551632811-561732d1e306?w=600&q=80' },
    { keys: ['vandring', 'hike', 'hiking'],             url: 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600&q=80' },
    { keys: ['cykel', 'mountainbike', 'mtb'],           url: 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa0?w=600&q=80' },
    { keys: ['camping', 'lejr', 'telt'],                url: 'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?w=600&q=80' },
    { keys: ['overlevelse', 'survival', 'bushcraft'],   url: 'https://images.unsplash.com/photo-1542396601-dca920ea2807?w=600&q=80' },
    { keys: ['zipline', 'trætop', 'højskov'],           url: 'https://images.unsplash.com/photo-1520208422220-d12a3c588574?w=600&q=80' },
    { keys: ['minigolf', 'golf', 'putting'],            url: 'https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=600&q=80' },
    { keys: ['gokart', 'gocart', 'karting', 'racing'],  url: '/images/gocart.png' },
    { keys: ['paintball'],                              url: '/images/paintball.png' },
    { keys: ['sumo', 'brydning', 'wrestling'],          url: '/images/sumo.png' },
    { keys: ['fodbold', 'bold'],                        url: 'https://images.unsplash.com/photo-1575361204480-aadea25e6e68?w=600&q=80' },
    { keys: ['tennis', 'padel', 'badminton'],           url: 'https://images.unsplash.com/photo-1622279457486-62dcc4a431d6?w=600&q=80' },
    { keys: ['bue', 'bueskydning', 'pil'],              url: 'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=600&q=80' },
    { keys: ['fiskeri', 'fisk', 'lystfiskeri'],         url: 'https://images.unsplash.com/photo-1516690561799-46d8f74f9abf?w=600&q=80' },
    { keys: ['faldskærm', 'skydive', 'freefall'],       url: 'https://images.unsplash.com/photo-1601024445121-e5b82f020549?w=600&q=80' },
    { keys: ['paraglid', 'svævefly', 'ballon'],         url: 'https://images.unsplash.com/photo-1499063078284-f78f7d89616a?w=600&q=80' },
    { keys: ['mad', 'kok', 'cooking', 'gastro'],        url: 'https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=600&q=80' },
    { keys: ['escape', 'gåde', 'puzzle'],               url: 'https://images.unsplash.com/photo-1629654857512-b8a6c8d4d9e4?w=600&q=80' },
    { keys: ['spa', 'wellness', 'afslapning'],          url: 'https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=600&q=80' },
];

const FALLBACK_IMAGES = [
    'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600&q=80',
    'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?w=600&q=80',
    'https://images.unsplash.com/photo-1551632811-561732d1e306?w=600&q=80',
];

function getImageForActivity(name) {
    const lower = name.toLowerCase();
    for (const entry of IMAGE_MAP) {
        if (entry.keys.some(k => lower.includes(k))) return entry.url;
    }
    const hash = [...name].reduce((acc, c) => acc + c.charCodeAt(0), 0);
    return FALLBACK_IMAGES[hash % FALLBACK_IMAGES.length];
}

const CARD_TAGS = ['Populær', 'Ny', 'Anbefalet', 'Eksklusiv', 'Bestseller', 'Sæson-hit'];

let selectedSlot = null;
let currentActivityName = '';
let currentActivityId   = null;
let currentProfile      = null; // Sættes ved DOMContentLoaded via GET /me

// ── HENT AKTIVITETER ────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {
    // Hent den loggede profil fra sessionen
    try {
        const res = await fetch('/me');
        if (res.ok) {
            currentProfile = await res.json();
        }
    } catch (e) {
        // Ingen session — currentProfile forbliver null
    }

    fetchActivities();

    // Sæt minimum dato til i dag
    const dateInput = document.getElementById('booking-date');
    if (dateInput) {
        const today = new Date().toISOString().split('T')[0];
        dateInput.min = today;
        dateInput.value = today;
    }
});

async function fetchActivities() {
    try {
        const response = await fetch('/activities');
        const activities = await response.json();
        render(activities);
    } catch (error) {
        // Demo-fallback hvis ingen server kører
        render(getDemoActivities());
    }
}

// ── AKTIVITETER GEMT GLOBALT (til modal-opslag via index) ─
let allActivities = [];
let allImages = [];

// ── RENDER KORT ─────────────────────────────────────────
function render(activities) {
    allActivities = activities;
    allImages = activities.map(act => getImageForActivity(act.name));

    const container = document.getElementById('dashboard');

    container.innerHTML = activities.map((act, i) => {
        const img = allImages[i];
        const tag = CARD_TAGS[i % CARD_TAGS.length];

        return `
      <div class="activity-card" onclick="openModal(${i})">
        <div class="card-image-wrap">
          <img class="card-image" src="${img}" alt="${act.name}" loading="lazy" />
          <div class="card-image-overlay"></div>
        </div>
        <div class="card-content">
          <span class="card-tag">${tag}</span>
          <h2>${act.name}</h2>
          <p>${act.description}</p>
          <div class="info-row">
            <span class="badge">👤 ${act.minParticipants}–${act.maxParticipants} pers.</span>
            <span class="badge">🎂 ${act.minAge}+ år</span>
          </div>
          <button
            class="btn-action"
            onclick="event.stopPropagation(); openModal(${i})">
            Book nu
          </button>
        </div>
      </div>
    `;
    }).join('');
}

// ── MODAL: ÅBEN ─────────────────────────────────────────
function openModal(index) {
    const act    = allActivities[index];
    const imgSrc = allImages[index];

    currentActivityName = act.name;
    currentActivityId   = act.id;
    selectedSlot        = null;

    document.getElementById('modal-img').src           = imgSrc;
    document.getElementById('modal-img').alt           = act.name;
    document.getElementById('modal-title').textContent = act.name;
    document.getElementById('modal-desc').textContent  = act.description;
    document.getElementById('modal-participants').textContent
        = `👤 ${act.minParticipants}–${act.maxParticipants} pers.`;
    document.getElementById('modal-age').textContent
        = `🎂 ${act.minAge}+ år`;

    // Sæt max deltagere fra aktiviteten
    const participantsInput = document.getElementById('booking-participants');
    participantsInput.min   = act.minParticipants;
    participantsInput.max   = act.maxParticipants;
    participantsInput.value = act.minParticipants;

    // Nulstil valgte slots
    document.querySelectorAll('.slot').forEach(s => s.classList.remove('selected'));

    document.getElementById('modal-overlay').classList.add('open');
    document.body.style.overflow = 'hidden';
}

// ── MODAL: LUK ──────────────────────────────────────────
function closeModal() {
    document.getElementById('modal-overlay').classList.remove('open');
    document.body.style.overflow = '';
}

// Luk med Escape
document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeModal();
});

// ── TIDSPUNKT VALG ──────────────────────────────────────
function selectSlot(btn) {
    document.querySelectorAll('.slot').forEach(s => s.classList.remove('selected'));
    btn.classList.add('selected');
    selectedSlot = btn.textContent;
}

// ── BOOKING BEKRÆFTELSE ─────────────────────────────────
async function confirmBooking() {
    const date         = document.getElementById('booking-date').value;
    const participants = parseInt(document.getElementById('booking-participants').value);

    if (!date) {
        shakeElement(document.querySelector('.date-picker'));
        return;
    }
    if (!selectedSlot) {
        shakeElement(document.querySelector('.time-slots'));
        return;
    }
    if (!participants || participants < 1) {
        shakeElement(document.getElementById('booking-participants'));
        return;
    }

    const btn = document.getElementById('confirm-btn');

    // Tjek om brugeren er logget ind
    if (!currentProfile) {
        showErrorToast('Du skal være logget ind for at booke');
        return;
    }

    btn.textContent = 'Sender...';
    btn.disabled    = true;

    // Byg startTime og endTime som LocalDateTime (aktivitet varer 2 timer)
    const [startHour, startMin] = selectedSlot.split(':').map(Number);
    const startTime = new Date(date);
    startTime.setHours(startHour, startMin, 0, 0);
    const endTime = new Date(startTime.getTime() + 2 * 60 * 60 * 1000);

    const toLocalDT = (d) => {
        const pad = n => String(n).padStart(2, '0');
        return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:00`;
    };

    // POST body matcher din Booking-model
    const booking = {
        activity:     { id: currentActivityId },
        profile:      { id: currentProfile.id },
        participants: participants,
        startTime:    toLocalDT(startTime),
        endTime:      toLocalDT(endTime)
    };

    try {
        const response = await fetch('/bookings', {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(booking)
        });

        if (!response.ok) throw new Error('Serverfejl');

        closeModal();
        showSuccessToast(currentActivityName, date, selectedSlot);

    } catch (error) {
        showErrorToast();
    } finally {
        btn.textContent = 'Bekræft booking →';
        btn.disabled    = false;
    }
}

// ── SUCCESS TOAST ───────────────────────────────────────
function showSuccessToast(name, date, slot) {
    const formatted = new Date(date).toLocaleDateString('da-DK', {
        weekday: 'long', day: 'numeric', month: 'long'
    });

    const toast = document.createElement('div');
    toast.className = 'toast toast-success';
    toast.innerHTML = `
    <div class="toast-icon">✓</div>
    <div class="toast-text">
      <strong>Booking bekræftet!</strong>
      <span>${name} — ${formatted} kl. ${slot}</span>
    </div>
  `;
    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add('toast-visible'), 10);
    setTimeout(() => {
        toast.classList.remove('toast-visible');
        setTimeout(() => toast.remove(), 400);
    }, 4000);
}

function showErrorToast(msg = 'Prøv igen eller kontakt os') {
    const toast = document.createElement('div');
    toast.className = 'toast toast-error';
    toast.innerHTML = `
    <div class="toast-icon">✕</div>
    <div class="toast-text">
      <strong>Noget gik galt</strong>
      <span>${msg}</span>
    </div>
  `;
    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add('toast-visible'), 10);
    setTimeout(() => {
        toast.classList.remove('toast-visible');
        setTimeout(() => toast.remove(), 400);
    }, 4000);
}

// ── HJÆLPEFUNKTION: RYS ANIMATION ───────────────────────
function shakeElement(el) {
    if (!el) return;
    el.style.animation = 'none';
    el.style.border = '1px solid #ef4444';
    setTimeout(() => { el.style.border = ''; }, 1500);
}

// ── DEMO DATA (fallback uden server) ────────────────────
function getDemoActivities() {
    return [
        {
            name: 'Klatring i Naturpark',
            description: 'Stig til tops i en af Danmarks smukkeste naturparker. Oplev friheden fra toppen og få adrenalinen til at pumpe.',
            minParticipants: 2, maxParticipants: 10, minAge: 12
        },
        {
            name: 'Havkajak Eventyr',
            description: 'Glid stille gennem fjorden og oplev kystlinjen fra vandsiden. Guidet tur med erfarne instruktører.',
            minParticipants: 2, maxParticipants: 8, minAge: 14
        },
        {
            name: 'Overlevelseskursus',
            description: 'Lær at bygge shelter, tænde ild og finde mad i naturen. Et kursus du aldrig glemmer.',
            minParticipants: 4, maxParticipants: 12, minAge: 16
        },
        {
            name: 'Mountainbike Trail',
            description: 'Tekniske stier gennem skov og bakker. Fra begynder til øvede — vi har ruterne der passer til dig.',
            minParticipants: 2, maxParticipants: 15, minAge: 10
        },
        {
            name: 'Dykkerkursus',
            description: 'Udforsk undervandsverdenen i krystalklart vand. PADI-certificeret kursus med alle materialer inkluderet.',
            minParticipants: 2, maxParticipants: 6, minAge: 15
        },
        {
            name: 'Zipline & Trætop',
            description: 'Sving dig fra trætop til trætop på vores spektakulære zipline-bane og oplev skoven fra fuglenes perspektiv.',
            minParticipants: 1, maxParticipants: 20, minAge: 8
        }
    ];
}