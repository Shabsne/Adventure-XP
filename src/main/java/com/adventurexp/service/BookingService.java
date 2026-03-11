package com.adventurexp.service;

import com.adventurexp.exceptions.BusinessLogicException;
import com.adventurexp.exceptions.ResourceNotFoundException;
import com.adventurexp.exceptions.ValidationException;
import com.adventurexp.model.Booking;
import com.adventurexp.model.BookingStatus;
import com.adventurexp.model.Profile;
import com.adventurexp.model.Activity;
import com.adventurexp.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ActivityService activityService;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(int id) {
        return bookingRepository.findById(id);
    }

    public Booking saveBooking(Booking booking) {
        validateAgeRequirements(booking);

        //Tjek om der er nok udstyr og kapacitet
        boolean hasCapacity = activityService.checkCapacity(
                booking.getActivity().getId(),
                booking.getParticipants()
        );

        if (!hasCapacity) {
            throw new BusinessLogicException(
                "Ikke nok operationelt udstyr eller for mange deltagere til denne aktivitet"
            );
        }

        return bookingRepository.save(booking);
    }

    public void deleteBooking(int id) {
        bookingRepository.deleteById(id);
    }

    //ISSUE #91
    private void validateAgeRequirements(Booking booking) {
        Activity activity = booking.getActivity();
        Profile profile = booking.getProfile();

        if (activity == null || profile == null) {
            throw new ValidationException("Booking skal have både en aktivitet og en profil.");
        }

        int customerAge = calculateAge(profile.getBirthDate());

        if (customerAge < activity.getMinAge()) {
            throw new ValidationException("For ung til denne aktivitet. Minimum: " + activity.getMinAge());
        }

        if (customerAge > activity.getMaxAge()) {
            throw new ValidationException("For gammel til denne aktivitet. Maksimumsalder: " + activity.getMaxAge());
        }
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            throw new ValidationException("Fødselsdato må ikke være null!");
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public void cancelNoShowBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : bookings) {
            boolean hasStarted = booking.getStartTime().isBefore(now);
            boolean notMarkedAsShowedUp = booking.getStatus() == BookingStatus.ACTIVE;

            if (hasStarted && notMarkedAsShowedUp) {
                booking.setStatus(BookingStatus.NO_SHOW);
                bookingRepository.save(booking);
            }
        }
    }

    // ISSUE #88
    public Booking updateBookingStatus(int bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking ikke fundet med id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED ||
            booking.getStatus() == BookingStatus.NO_SHOW) {
            throw new BusinessLogicException("Kan ikke ændre status på en aflyst booking.");
        }

        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}
