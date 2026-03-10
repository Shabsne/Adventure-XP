package com.adventurexp.config;

import com.adventurexp.model.Activity;
import com.adventurexp.model.Equipment;
import com.adventurexp.repository.ActivityRepository;
import com.adventurexp.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements CommandLineRunner {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Override
    public void run(String... args) throws Exception {

        if (activityRepository.count() == 0) {
            System.out.println("Initialiserer AdventureXP data...");

            // --- 1. GOCART ---
            Activity gocart = new Activity("Gocart", "Hæsblæsende ræs på banen", 5, 12, 18);
            activityRepository.save(gocart);

            // Tilføj 5 gokarts (alle OK)
            for (int i = 1; i <= 5; i++) {
                equipmentRepository.save(new Equipment("Gocart #" + i, "270cc model", true, gocart));
            }
            // Tilføj 1 defekt gokart (Test af #94)
            equipmentRepository.save(new Equipment("Gocart #6", "Defekt bremsekabel", false, gocart));


            // --- 2. PAINTBALL ---
            Activity paintball = new Activity("Paintball", "Action og taktik i skoven", 6, 20, 16);
            activityRepository.save(paintball);

            for (int i = 1; i <= 8; i++) {
                equipmentRepository.save(new Equipment("Paintball Gevær #" + i, "Tippmann A5", true, paintball));
            }


            // --- 3. MINIGOLF ---
            Activity minigolf = new Activity("Minigolf", "Hyggelig 18-hullers bane", 1, 6, 5);
            activityRepository.save(minigolf);

            equipmentRepository.save(new Equipment("Minigolf Sæt", "Køller og bolde", true, minigolf));


            // --- 4. SUMO WRESTLING ---
            Activity sumo = new Activity("Sumo Wrestling", "Sjov brydning i store dragter", 2, 2, 12);
            activityRepository.save(sumo);

            equipmentRepository.save(new Equipment("Sumo Dragter", "To store dragter inkl. måtter", true, sumo));

            System.out.println("AdventureXP data er indlæst succesfuldt!");
        }
    }
}