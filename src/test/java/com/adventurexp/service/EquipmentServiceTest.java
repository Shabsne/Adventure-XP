package com.adventurexp.service;

import com.adventurexp.model.Equipment;
import com.adventurexp.repository.EquipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    @Test
    void testUpdateStatusToDefective() {
        // Arrange
        Equipment gokart = new Equipment("Gokart", "Fast one", true);
        gokart.setId(1);
        when(equipmentRepository.findById(1)).thenReturn(Optional.of(gokart));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(gokart);

        // Act
        Equipment updated = equipmentService.updateStatus(1, false);

        // Assert
        assertFalse(updated.isOperational());
        verify(equipmentRepository, times(1)).save(gokart);
    }
}