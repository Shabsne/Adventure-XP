package com.adventurexp.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;
    private int minParticipants;
    private int maxParticipants;
    private int duration;
    private int minAge;
    private int maxAge;


    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Equipment> equipments;

    // for datainits skyld
    public Activity(String name, String description, int minParticipants, int maxParticipants, int minAge) {
        this.name = name;
        this.description = description;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.minAge = minAge;
    }

    public Activity() {}

    public Activity(int id, String name, String description, int minParticipants, int maxParticipants, int duration, int minAge, int maxAge,
                    List<Booking> bookings, List<Equipment> equipments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.duration = duration;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.bookings = bookings;
        this.equipments = equipments;
    }

    // --- GETTERS & SETTERS ---
    public int getId() { return id; }
    public int getDuration() {return duration;}
    public List<Equipment> getEquipments() {return equipments;}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public List<Booking> getBookings() { return bookings; }
    public int getMinParticipants() {return minParticipants;}
    public int getMaxParticipants() {return maxParticipants;}
    public int getMinAge() {return minAge;}
    public int getMaxAge() {return maxAge;}


    public void setEquipments(List<Equipment> equipments) {this.equipments = equipments;}
    public void setId(int id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setMinParticipants(int minParticipants) { this.minParticipants = minParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setMinAge(int minAge) { this.minAge = minAge; }
    public void setMaxAge(int maxAge) { this.maxAge = maxAge; }
}