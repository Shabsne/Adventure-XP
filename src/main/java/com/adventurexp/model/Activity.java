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

    // Relation til udstyr (En aktivitet har mange stykker udstyr)
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Equipment> equipments = new ArrayList<>();

    // Relation til bookinger (En aktivitet har mange bookinger)
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    // VIGTIGT: JPA kræver en tom constructor
    public Activity() {}

    public Activity(String name, String description, int minParticipants, int maxParticipants, int duration, int minAge, int maxAge) {
        this.name = name;
        this.description = description;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.duration = duration;
        this.minAge = minAge;
        this.maxAge = maxAge;
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