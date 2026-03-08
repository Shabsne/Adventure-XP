package com.adventurexp.repository;

import com.adventurexp.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Fortæller Spring, at denne klasse håndterer database-adgang
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {

    // Ved at arve fra JpaRepository får du automatisk adgang til:
    // .save(equipment)    <- Bruges til både opret og opdater (Issue #94, #95)
    // .findAll()          <- Henter alt udstyr
    // .findById(id)       <- Finder et specifikt stykke udstyr
    // .deleteById(id)     <- Sletter udstyr

}