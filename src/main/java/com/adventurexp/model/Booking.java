package com.adventurexp.model;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private Profile profile;
    private Activity activity
    private int participants;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

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

    @Override
    public String toString() {
        return "Booking{id=" + id + ", profile= " + profile + ", participants= " + participants
                + ", startTime= " + startTime + ", endTime= " + endTime + "}";
    }
