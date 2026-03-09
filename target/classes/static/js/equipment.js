const apiUrl = '/api/equipment';

// 1. Hent alt udstyr når siden indlæses
document.addEventListener('DOMContentLoaded', fetchEquipment);

async function fetchEquipment() {
    const response = await fetch(apiUrl);
    const data = await response.json();
    displayEquipment(data);
}

// 2. Vis udstyr i tabellen
function displayEquipment(equipmentList) {
    const tbody = document.querySelector('#equipmentTable tbody');
    tbody.innerHTML = '';

    equipmentList.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.name}</td>
            <td>${item.description}</td>
            <td class="${item.operational ? 'operational' : 'defective'}">
                ${item.operational ? 'OK' : 'DEFECTIVE'}
            </td>
            <td>
                <button onclick="toggleStatus(${item.id}, ${!item.operational})">
                    Mark as ${item.operational ? 'Defective' : 'OK'}
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// 3. Opdater status via API (Issue #94 & #95)
async function toggleStatus(id, newStatus) {
    // Vi kalder din PatchMapping i Controlleren
    const response = await fetch(`${apiUrl}/${id}/status?operational=${newStatus}`, {
        method: 'PATCH'
    });

    if (response.ok) {
        fetchEquipment(); // Opdater listen
    } else {
        alert("Kunne ikke opdatere status. Er du logget ind som service-medarbejder?");
    }
}