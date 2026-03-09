package com.adventurexp.service;

import com.adventurexp.model.Equipment;
import com.adventurexp.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Fortæller Spring, at dette er en Service-klasse med forretningslogik
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

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
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));

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
}