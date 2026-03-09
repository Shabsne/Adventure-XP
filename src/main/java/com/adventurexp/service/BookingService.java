package com.adventurexp.service;

import com.adventurexp.model.Booking;
import com.adventurexp.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(int id) {
        return bookingRepository.findById(id);
    }

    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public void deleteBooking(int id) {
        bookingRepository.deleteById(id);
    }

    private void validateAgeRequirements(Booking booking) {
        Activity activity = booking.getActivity();
        Profile profile = booking.getProfile();

        if (activity == null || profile == null) {
            throw new IllegalArgumentException("Booking skal have både en aktivitet og en profil.");
        }

        int customerAge = calculateAge(profile.getAge());

        if (customerAge < activity.getMinAge()) {
            throw new IllegalArgumentException("For ung til denne aktivitet. Minimum: " + activity.getMinAge());
        }

        if (customerAge > activity.getMaxAge()) {
            throw new IllegalArgumentException("For gammel til denne aktivitet. Maksimumsalder: " + activity.getMaxAge());
        }

        private int calculateAge(LocalDate birthDate) {
            if (birthDate == null) {
                throw new IllegalArgumentException("Fødselsdato må ikke være null!");
            }
            return Period.between(birthDate, LocalDate.now()).getYears();
        }
    }
}
