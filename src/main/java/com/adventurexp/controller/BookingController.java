package com.adventurexp.controller;

import com.adventurexp.model.Booking;
import com.adventurexp.model.BookingStatus;
import com.adventurexp.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    //Hent alle bookinger
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    //Hent en specifik booking
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable int id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Opret en ny booking
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        try {
            Booking saved = bookingService.saveBooking(booking);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Slet en booking
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable int id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    // ISSUE #88 - Tjek ind (mødt op)
    @PutMapping("/{id}/checkin")
    public ResponseEntity<Booking> checkIn(@PathVariable int id) {
        try {
            Booking updated = bookingService.updateBookingStatus(id, BookingStatus.COMPLETED);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ISSUE #88 - Aflys ved udeblivelse
    @PutMapping("/{id}/noshow")
    public ResponseEntity<Booking> noShow(@PathVariable int id) {
        try {
            Booking updated = bookingService.updateBookingStatus(id, BookingStatus.NO_SHOW);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
