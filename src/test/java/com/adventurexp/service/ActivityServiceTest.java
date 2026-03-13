package com.adventurexp.service;

import com.adventurexp.model.Activity;
import com.adventurexp.model.Equipment;
import com.adventurexp.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {


    @Mock
    private ActivityRepository activityRepository;


    @InjectMocks
    private ActivityService activityService;
    private Activity activity;
    private Equipment operationalEquipment;
    private Equipment brokenEquipment;


    // BeforeEach nulstiller data før hver test så de ikke spænder ben for hinanden
    @BeforeEach
    void setUp() {
        // Lav en test-aktivitet: Gocart, 2-12 deltagere, alder 10-99
        activity = new Activity();
        activity.setId(1);
        activity.setName("Gocart");
        activity.setDescription("Hæsblæsende ræs på banen");
        activity.setMinParticipants(2);
        activity.setMaxParticipants(12);
        activity.setMinAge(10);
        activity.setMaxAge(99);
        activity.setDuration(60);

        // Et stykke udstyr der virker
        operationalEquipment = new Equipment();
        operationalEquipment.setOperational(true);

        // Et stykke udstyr der er i stykker
        brokenEquipment = new Equipment();
        brokenEquipment.setOperational(false);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 1: readActivity() — henter en aktivitet korrekt
    // Vi fortæller den falske repository at den skal returnere
    // vores test-aktivitet når der søges efter id=1.
    // Derefter tjekker vi at service returnerer det rigtige objekt.
    // ─────────────────────────────────────────────────────────────
    @Test
    void readActivity_returnsActivity_whenExists() {
        when(activityRepository.findById(1)).thenReturn(Optional.of(activity));

        Activity result = activityService.readActivity(1);

        assertNotNull(result);
        assertEquals("Gocart", result.getName());
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 2: readActivity() — returnerer null hvis aktivitet ikke findes
    // ─────────────────────────────────────────────────────────────
    @Test
    void readActivity_returnsNull_whenNotExists() {
        when(activityRepository.findById(99)).thenReturn(Optional.empty());

        Activity result = activityService.readActivity(99);

        assertNull(result);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 3: readAllActivities() — returnerer alle aktiviteter
    // ─────────────────────────────────────────────────────────────
    @Test
    void readAllActivities_returnsAllActivities() {
        Activity activity2 = new Activity();
        activity2.setName("Paintball");

        when(activityRepository.findAll()).thenReturn(List.of(activity, activity2));

        List<Activity> result = activityService.readAllActivities();

        assertEquals(2, result.size());
        assertEquals("Gocart", result.get(0).getName());
        assertEquals("Paintball", result.get(1).getName());
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 4: getReadyEquipmentCount() — tæller kun operationelt udstyr
    // Vi giver aktiviteten 2 fungerende og 1 defekt stykke udstyr.
    // Vi forventer at metoden returnerer 2.
    // ─────────────────────────────────────────────────────────────
    @Test
    void getReadyEquipmentCount_countsOnlyOperationalEquipment() {
        activity.setEquipments(List.of(operationalEquipment, operationalEquipment, brokenEquipment));
        when(activityRepository.findById(1)).thenReturn(Optional.of(activity));

        int result = activityService.getReadyEquipmentCount(1);

        assertEquals(2, result);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 5: checkCapacity() — godkender booking når der er nok udstyr
    // 5 fungerende udstyr, vi beder om 4 deltagere → skal godkendes
    // ─────────────────────────────────────────────────────────────
    @Test
    void checkCapacity_returnsTrue_whenEnoughEquipment() {
        activity.setEquipments(List.of(
                operationalEquipment, operationalEquipment, operationalEquipment,
                operationalEquipment, operationalEquipment
        ));
        when(activityRepository.findById(1)).thenReturn(Optional.of(activity));

        boolean result = activityService.checkCapacity(1, 4);

        assertTrue(result);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 6: checkCapacity() — afviser booking når der ikke er nok udstyr
    // 1 fungerende udstyr, vi beder om 5 deltagere → skal afvises
    // ─────────────────────────────────────────────────────────────
    @Test
    void checkCapacity_returnsFalse_whenNotEnoughEquipment() {
        activity.setEquipments(List.of(operationalEquipment));
        when(activityRepository.findById(1)).thenReturn(Optional.of(activity));

        boolean result = activityService.checkCapacity(1, 5);

        assertFalse(result);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 7: checkCapacity() — afviser booking når deltagerantal
    // er under minimumskravet (under minParticipants=2)
    // ─────────────────────────────────────────────────────────────
    @Test
    void checkCapacity_returnsFalse_whenBelowMinParticipants() {
        activity.setEquipments(List.of(
                operationalEquipment, operationalEquipment, operationalEquipment
        ));
        when(activityRepository.findById(1)).thenReturn(Optional.of(activity));

        boolean result = activityService.checkCapacity(1, 1);

        assertFalse(result);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 8: checkCapacity() — afviser booking når deltagerantal
    // overstiger maksimum (over maxParticipants=12)
    // ─────────────────────────────────────────────────────────────
    @Test
    void checkCapacity_returnsFalse_whenAboveMaxParticipants() {
        // Lav 15 fungerende udstyr
        List<Equipment> lotsOfEquipment = java.util.Collections.nCopies(15, operationalEquipment);
        activity.setEquipments(lotsOfEquipment);
        when(activityRepository.findById(1)).thenReturn(Optional.of(activity));

        boolean result = activityService.checkCapacity(1, 13);

        assertFalse(result);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 9: getActivityStatus() — returnerer "KLAR"
    // når der er nok operationelt udstyr
    // ─────────────────────────────────────────────────────────────
    @Test
    void getActivityStatus_returnsKLAR_whenEnoughEquipment() {
        activity.setEquipments(List.of(
                operationalEquipment, operationalEquipment, operationalEquipment
        ));
        when(activityRepository.findById(1)).thenReturn(Optional.of(activity));

        String status = activityService.getActivityStatus(1);

        assertEquals("KLAR", status);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 10: getActivityStatus() — returnerer "LUKKET"
    // når der ikke er nok operationelt udstyr til minimum deltagere
    // minParticipants=2, men kun 1 fungerende udstyr → LUKKET
    // ─────────────────────────────────────────────────────────────
    @Test
    void getActivityStatus_returnsLUKKET_whenNotEnoughEquipment() {
        activity.setEquipments(List.of(operationalEquipment)); // kun 1, men min er 2
        when(activityRepository.findById(1)).thenReturn(Optional.of(activity));

        String status = activityService.getActivityStatus(1);

        assertEquals("LUKKET", status);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 11: createActivity() — kalder repository.save() én gang
    // ─────────────────────────────────────────────────────────────
    @Test
    void createActivity_callsSaveOnce() {
        activityService.createActivity(activity);

        // verify() tjekker at save() faktisk blev kaldt præcis én gang
        verify(activityRepository, times(1)).save(activity);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 12: deleteActivity() — kalder repository.deleteById() én gang
    // ─────────────────────────────────────────────────────────────
    @Test
    void deleteActivity_callsDeleteByIdOnce() {
        activityService.deleteActivity(1);

        verify(activityRepository, times(1)).deleteById(1);
    }
}
