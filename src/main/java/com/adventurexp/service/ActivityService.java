package com.adventurexp.service;

import com.adventurexp.model.Activity;
import com.adventurexp.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    // CREATE
    public void createActivity(Activity activity) {
        activityRepository.save(activity);
    }

    // READ
    public Activity readActivity(int id) {
        return activityRepository.findById(id).orElse(null);
    }

    public List<Activity> readAllActivities() {
        return activityRepository.findAll();
    }

    // UPDATE
    public void updateActivity(int id, String newName, String newDesc, int minP, int maxP, int dur, int minA, int maxA) {
        // Find den eksisterende aktivitet i databasen
        Optional<Activity> activityData = activityRepository.findById(id);

        if (activityData.isPresent()) {
            Activity a = activityData.get();

            // Opdater værdierne
            a.setName(newName);
            a.setDescription(newDesc);
            a.setMinParticipants(minP);
            a.setMaxParticipants(maxP);
            a.setDuration(dur);
            a.setMinAge(minA);
            a.setMaxAge(maxA);

            // Gem ændringerne tilbage i databasen
            activityRepository.save(a);
            System.out.println("Aktivitet med ID " + id + " blev opdateret i databasen.");
        } else {
            System.out.println("Fejl: Kunne ikke finde aktivitet med ID " + id);
        }
    }

    // DELETE
    public void deleteActivity(int id) {
        activityRepository.deleteById(id);
    }

    public int getReadyEquipmentCount(int activityId) {
        Activity activity = readActivity(activityId);
        if (activity != null) {
            return (int) activity.getEquipments().stream()
                    .filter(e -> !e.isOperational())
                    .count();
        }
        return 0;
    }
}