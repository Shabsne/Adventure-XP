package com.adventurexp.service;

import com.adventurexp.exceptions.BusinessLogicException;
import com.adventurexp.model.*;
import com.adventurexp.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private BookingService bookingService;

    private Activity activity;
    private Profile profile;
    private Booking activeBooking;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        activity = new Activity();
        activity.setId(1);
        activity.setName("Gocart");
        activity.setDescription("Hæsblæsende ræs på banen");
        activity.setMinParticipants(2);
        activity.setMaxParticipants(12);
        activity.setMinAge(10);
        activity.setMaxAge(99);
        activity.setDuration(60);

        profile = new Profile();
        profile.setId(1);
        profile.setName("Test Bruger");
        profile.setBirthDate(LocalDate.of(1995, 5, 15)); // 30 år gammel
        profile.setRole(Role.Custommer);

        startTime = LocalDateTime.of(2025, 6, 1, 10, 0);
        endTime = LocalDateTime.of(2025, 6, 1, 11, 0);

        activeBooking = new Booking();
        activeBooking.setId(1);
        activeBooking.setProfile(profile);
        activeBooking.setActivity(activity);
        activeBooking.setParticipants(4);
        activeBooking.setStartTime(startTime);
        activeBooking.setEndTime(endTime);
        activeBooking.setStatus(BookingStatus.ACTIVE);
        activeBooking.setExclusive(false);
    }

    @Test
    void getAllBookings_returnsAllBookings() {
        Booking booking2 = new Booking();
        booking2.setId(2);

        when(bookingRepository.findAll()).thenReturn(List.of(activeBooking, booking2));

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void getBookingById_returnsBooking_whenExists() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(activeBooking));

        Optional<Booking> result = bookingService.getBookingById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void getBookingById_returnsEmpty_whenNotExists() {
        when(bookingRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Booking> result = bookingService.getBookingById(99);

        assertFalse(result.isPresent());
    }

    @Test
    void createNormalBooking_createsBooking_whenNoConflict() {
        when(bookingRepository.findAll()).thenReturn(Collections.emptyList());
        when(activityService.checkCapacity(1, 4)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenReturn(activeBooking);

        Booking result = bookingService.createNormalBooking(activeBooking);

        assertNotNull(result);
        assertEquals(BookingStatus.ACTIVE, result.getStatus());
        assertFalse(result.isExclusive());
        verify(bookingRepository, times(1)).save(activeBooking);
    }

    @Test
    void createNormalBooking_throwsException_whenExclusiveConflict() {
        Booking exclusiveBooking = new Booking();
        exclusiveBooking.setId(2);
        exclusiveBooking.setActivity(activity);
        exclusiveBooking.setStartTime(startTime);
        exclusiveBooking.setEndTime(endTime);
        exclusiveBooking.setStatus(BookingStatus.ACTIVE);
        exclusiveBooking.setExclusive(true);
        exclusiveBooking.setGroupName("Burger Banditten");

        when(bookingRepository.findAll()).thenReturn(List.of(exclusiveBooking));

        // Vi forventer en BusinessLogicException med firmanavet i beskeden
        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class, () -> bookingService.createNormalBooking(activeBooking)
        );

        assertTrue(exception.getMessage().contains("Burger Banditten"));
    }

    @Test
    void createNormalBooking_throwsException_whenNotEnoughCapacity() {
        when(bookingRepository.findAll()).thenReturn(Collections.emptyList());
        when(activityService.checkCapacity(1, 4)).thenReturn(false);

        assertThrows(BusinessLogicException.class, () -> bookingService.createNormalBooking(activeBooking));

        verify(bookingRepository, never()).save(any());
    }
}
