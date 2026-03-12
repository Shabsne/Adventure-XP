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

    public Booking findExclusiveBookingInTimeRange(int activityId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> bookings = bookingRepository.findAll();

        for (Booking booking : bookings) {
            //Tjek om det er samme aktivitet
            if (booking.getActivity().getId() != activityId ||
            !booking.isExclusive() ||
            booking.getStatus() != BookingStatus.ACTIVE) {
                continue;
            }

            if (booking.getStartTime().isBefore(endTime) && booking.getEndTime().isAfter(startTime)) {
                return booking;
            }
        }

        return null; // Ingen eksklusiv booking fundet
    }

    // Tjek om aktiviteten er eksklusivt booket
    public boolean isActivityBookedExclusively(int activityId, LocalDateTime startTime, LocalDateTime endTime) {
        return findExclusiveBookingInTimeRange(activityId, startTime, endTime) != null;
    }

    public String findExclusiveGroupName(int activityId, LocalDateTime startTime, LocalDateTime endTime) {
        Booking exclusiveBooking = findExclusiveBookingInTimeRange(activityId, startTime, endTime);
        return exclusiveBooking != null ? exclusiveBooking.getGroupName() : null;
    }

    // Opret en normal booking, men tjek først om der er eksklusiv booking
    public Booking createNormalBooking(Booking booking) {
        Activity activity = booking.getActivity();

        // Tjek om aktivitet er booket eksklusivt
        if (isActivityBookedExclusively(activity.getId(), booking.getStartTime(), booking.getEndTime())) {
            String groupName = findExclusiveGroupName(activity.getId(), booking.getStartTime(), booking.getEndTime());
            throw new BusinessLogicException("Aktiviteten er eksklusivt booket af " + groupName + " i dette tidsrum.");
        }
        booking.setExclusive(false);

        // Tjek om der er nok udstyr og kapacitet
        boolean hasCapacity = activityService.checkCapacity(booking.getActivity().getId(), booking.getParticipants());

        if (!hasCapacity) {
            throw new BusinessLogicException("Ikke nok operationelt udstyr eller for mange bookings på denne aktivitet i dette tidsrum.");
        }

        booking.setStatus(BookingStatus.ACTIVE);
        return bookingRepository.save(booking);
    }

    // Opret en eksklusiv booking
    public Booking createExclusiveBooking(Booking booking, String groupName) {
        Activity activity = booking.getActivity();

        // Tjek hvis der allerede er eksklusiv booking i det tidsrum
        if (isActivityBookedExclusively(activity.getId(), booking.getStartTime(), booking.getEndTime())) {
            String groupName2 = findExclusiveGroupName(activity.getId(), booking.getStartTime(), booking.getEndTime());
            throw new BusinessLogicException("Aktiviteten er eksklusivt booket af " + groupName2 + " i dette tidsrum.");
        }

        booking.setExclusive(true);
        booking.setGroupName(groupName);
        booking.setStatus(BookingStatus.ACTIVE);
        return bookingRepository.save(booking);
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
