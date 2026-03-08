package com.adventurexp.config;

import com.adventurexp.model.Equipment;
import com.adventurexp.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements CommandLineRunner {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Override
    public void run(String... args) throws Exception {
        // Vi opretter kun data, hvis tabellen er tom
        if (equipmentRepository.count() == 0) {
            System.out.println("Opretter test-udstyr i databasen...");

            // Gokarts
            for (int i = 1; i <= 10; i++) {
                equipmentRepository.save(new Equipment("Gokart #" + i, "Standard 270cc", true));
            }

            // Defekt udstyr til test (#94)
            equipmentRepository.save(new Equipment("Gokart #11", "Trænger til ny motor", false));

            // Paintball udstyr
            for (int i = 1; i <= 5; i++) {
                equipmentRepository.save(new Equipment("Paintball Gevær #" + i, "Tippmann A5", true));
            }

            System.out.println("Data er klar!");
        }
    }
}