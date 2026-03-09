package com.adventurexp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // I Booking.java
    @ManyToOne
    @JoinColumn(name = "profile_id") // Dette fortæller databasen hvordan den skal linke
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "activity_id") // Dette laver fremmednøglen i DB
    private Activity activity;
    private int participants;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;

    public Booking() {

    }

    public Booking(int id, Profile profile, Activity activity, int participants, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.profile = profile;
        this.participants = participants;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking{id=" + id + ", profile= " + profile + ", participants= " + participants
                + ", startTime= " + startTime + ", endTime= " + endTime + "}";
    }
}
