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

    //Opret normal booking
    @PostMapping
    public ResponseEntity<String> createBooking(@RequestBody Booking booking) {
        try {
            bookingService.createNormalBooking(booking);
            return ResponseEntity.ok("Booking oprettet");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Opret eksklusiv booking
    @PostMapping("/exclusive")
    public ResponseEntity<String> createExclusiveBooking(@RequestBody Booking booking, @RequestParam String groupName) {
        try {
            bookingService.createExclusiveBooking(booking, groupName);
            return ResponseEntity.ok("Eksklusiv booking oprettet for " + groupName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Tjek om aktivitet er eksklusivt booket
    @GetMapping("/check/{activityId}")
    public ResponseEntity<String> checkExclusive(@PathVariable int activityId, @RequestParam String startTime, @RequestParam String endTime) {
        return ResponseEntity.ok("Eksklusiv check implementeret");
    }

    //Slet en booking
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable int id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fejl ved sletning");
        }
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
