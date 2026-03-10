document.addEventListener('DOMContentLoaded', fetchActivities);

async function fetchActivities() {
    const response = await fetch('/activities');
    const activities = await response.json();
    renderDashboard(activities);
}

function renderDashboard(activities) {
    const container = document.getElementById('dashboard');
    container.innerHTML = '';

    activities.forEach(activity => {
        const isClosed = activity.equipments.filter(e => e.operational).length < activity.minParticipants;

        const card = document.createElement('div');
        card.className = 'card';
        card.innerHTML = `
            <h2>${activity.name} 
                <span class="status-badge ${isClosed ? 'status-error' : 'status-ok'}">
                    ${isClosed ? 'LUKKET' : 'KLAR'}
                </span>
            </h2>
            <p>${activity.description}</p>
            <p><strong>Deltagere:</strong> ${activity.minParticipants} - ${activity.maxParticipants}</p>
            
            <div class="equipment-list">
                <h4>Udstyr Status:</h4>
                ${activity.equipments.map(eq => `
                    <div class="equipment-item">
                        <span>${eq.name} ${eq.operational ? '✅' : '❌'}</span>
                        ${!eq.operational ? `<button class="repair-btn" onclick="repair(${activity.id}, ${eq.id})">Gør OK</button>` : ''}
                    </div>
                `).join('')}
            </div>
        `;
        container.appendChild(card);
    });
}

async function repair(activityId, equipmentId) {
    const userId = document.getElementById('userIdInput').value;

    // Vi bruger PATCH endpointet vi lige har lavet i din Controller
    const response = await fetch(`/activities/${activityId}/equipment/${equipmentId}/status?status=true&userId=${userId}`, {
        method: 'PATCH'
    });

    if (response.ok) {
        alert("Udstyr repareret!");
        fetchActivities(); // Opdater dashboardet så vi ser ændringen
    } else if (response.status === 403) {
        alert("FEJL: Du har ikke 'Service' rettigheder!");
    } else {
        alert("Der skete en fejl.");
    }
}