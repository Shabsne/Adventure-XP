package com.adventurexp.controller;

import com.adventurexp.model.Activity;
import com.adventurexp.model.Profile;
import com.adventurexp.service.ActivityService;
import com.adventurexp.service.EquipmentService;
import com.adventurexp.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ProfileService profileService; 
    @Autowired
    private EquipmentService equipmentService;

    // CREATE:
    @PostMapping("/add")
    public ResponseEntity<String> createActivity(@RequestBody Activity activity) {
        activityService.createActivity(activity);
        return new ResponseEntity<>("Aktivitet oprettet med succes", HttpStatus.CREATED);
    }

    // READ ALL:
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        List<Activity> activities = activityService.readAllActivities();
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }

    // READ BY ID:
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable int id) {
        Activity activity = activityService.readActivity(id);
        if (activity != null) {
            return new ResponseEntity<>(activity, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // UPDATE:
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateActivity(@PathVariable int id, @RequestBody Activity activity) {
        activityService.updateActivity(
                id,
                activity.getName(),
                activity.getDescription(),
                activity.getMinParticipants(),
                activity.getMaxParticipants(),
                activity.getDuration(),
                activity.getMinAge(),
                activity.getMaxAge()
        );
        return new ResponseEntity<>("Aktivitet opdateret", HttpStatus.OK);
    }

    // DELETE:
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable int id) {
        activityService.deleteActivity(id);
        return new ResponseEntity<>("Aktivitet slettet", HttpStatus.OK);
    }

    // Hent antal klar udstyr for en aktivitet
    @GetMapping("/{id}/ready-equipment")
    public ResponseEntity<Integer> getReadyEquipment(@PathVariable int id) {
        int count = activityService.getReadyEquipmentCount(id);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    // PATCH: Opdater udstyrsstatus med rolletjek
    @PatchMapping("/{activityId}/equipment/{equipmentId}/status")
    public ResponseEntity<String> updateEquipmentStatus(
            @PathVariable int activityId,
            @PathVariable int equipmentId,
            @RequestParam boolean status,
            @RequestParam int userId) {

        try {
            Profile user = profileService.readProfile(userId);

            if (user == null) {
                return new ResponseEntity<>("Bruger ikke fundet", HttpStatus.NOT_FOUND);
            }

            equipmentService.updateEquipmentStatus(activityId, equipmentId, status, user);
            return new ResponseEntity<>("Status opdateret", HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}