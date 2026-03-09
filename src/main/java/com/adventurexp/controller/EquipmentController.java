package com.adventurexp.controller;

import com.adventurexp.model.Equipment;
import com.adventurexp.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment") // Grund-stien til dit API
@CrossOrigin // Gør det muligt for din frontend at kalde API'et uden CORS-fejl
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    // Henter alle stykker udstyr (GET http://localhost:8080/api/equipment)
    @GetMapping
    public List<Equipment> getAllEquipment() {
        return equipmentService.getAllEquipment();
    }

    // Opretter nyt udstyr (POST http://localhost:8080/api/equipment)
    @PostMapping
    public Equipment createEquipment(@RequestBody Equipment equipment) {
        return equipmentService.saveEquipment(equipment);
    }

    // ISSUE #94 & #95: Opdaterer status på udstyr
    // Vi bruger PATCH her, da vi kun opdaterer ét felt (operational)
    // Kald: PATCH http://localhost:8080/api/equipment/5/status?operational=false
    @PatchMapping("/{id}/status")
    public ResponseEntity<Equipment> updateStatus(
            @PathVariable int id,
            @RequestParam boolean operational) {

        try {
            Equipment updated = equipmentService.updateStatus(id, operational);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Sletter udstyr (DELETE http://localhost:8080/api/equipment/5)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable int id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}