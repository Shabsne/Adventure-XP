package com.adventurexp.service;

import com.adventurexp.exceptions.ResourceNotFoundException;
import com.adventurexp.exceptions.UnauthorizedException;
import com.adventurexp.model.Activity;
import com.adventurexp.model.Equipment;
import com.adventurexp.model.Profile;
import com.adventurexp.model.Role;
import com.adventurexp.repository.ActivityRepository;
import com.adventurexp.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Fortæller Spring, at dette er en Service-klasse med forretningslogik
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ActivityRepository activityRepository;

    // Henter alt udstyr (God til overblikssider)
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    // Finder ét specifikt stykke udstyr
    public Optional<Equipment> getEquipmentById(int id) {
        return equipmentRepository.findById(id);
    }

    // Opretter eller gemmer udstyr
    public Equipment saveEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    // Sletter udstyr
    public void deleteEquipment(int id) {
        equipmentRepository.deleteById(id);
    }

    // ISSUES #94 & #95: Opdaterer status (Defekt/OK)
    // Denne metode bruges når en medarbejder trykker "Defekt" eller "OK" på deres tablet
    public Equipment updateStatus(int id, boolean isOperational) {
        // Vi finder først udstyret i databasen
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));

        // Vi ændrer status og gemmer igen
        equipment.setOperational(isOperational);
        return equipmentRepository.save(equipment);
    }

    // ISSUE #96: Validering af kapacitet
    // Denne metode kan bruges af Booking-delen til at se, om der er nok virkende udstyr
    public boolean isCapacitySufficient(String equipmentName, int requiredAmount) {
        List<Equipment> allEquipment = equipmentRepository.findAll();

        // Vi tæller hvor mange stykker udstyr med det navn, der rent faktisk virker
        long count = allEquipment.stream()
                .filter(e -> e.getName().equalsIgnoreCase(equipmentName))
                .filter(Equipment::isOperational) // Kun dem hvor operational == true
                .count();

        return count >= requiredAmount;
    }

    public void updateEquipmentStatus(int activityId, int equipmentId, boolean newStatus, Profile user) {
        // 1. Sikkerhedstjek: Kun rollen 'Service' må gøre udstyr "OK" igen (re-aktivere)
        if (newStatus && user.getRole() != Role.Service) {
            throw new UnauthorizedException("Adgang nægtet: Kun Service-medarbejdere må godkende defekt udstyr.");
        }

        // 2. Find aktiviteten (vi bruger activityRepository her)
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Aktivitet ikke fundet"));

        // 3. Find det specifikke stykke udstyr i aktivitetens liste
        Equipment equipment = activity.getEquipments().stream()
                .filter(e -> e.getId() == equipmentId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Udstyret blev ikke fundet under denne aktivitet"));

        // 4. Opdater status
        equipment.setOperational(newStatus);

        // 5. Gem via Activity (pga. CascadeType.ALL i din model)
        activityRepository.save(activity);
    }


}