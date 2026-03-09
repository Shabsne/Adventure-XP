package com.adventurexp.model;

import jakarta.persistence.*; // Importerer JPA annotationer

@Entity // Fortæller Spring, at denne klasse skal være en tabel i databasen
@Table(name = "equipment") // Bestemmer at tabellen skal hedde 'equipment'
public class Equipment {

    @Id // Markerer dette felt som Primary Key (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Sørger for Auto-increment (1, 2, 3...)
    private int id;

    @Column(nullable = false) // Navnet må ikke være tomt
    private String name;

    @Column(length = 500) // Giver plads til en længere beskrivelse
    private String description;

    // Dette felt er kernen i din opgave (#94, #95, #96)
    // Det styrer om udstyret tæller med i kapaciteten for en aktivitet
    @Column(nullable = false)
    private boolean operational;

    @ManyToOne
    @JoinColumn(name = "activity_id") // Dette opretter en fremmednøgle i databasen
    private Activity activity;

    // --- CONSTRUCTORS ---

    // VIGTIGT: JPA kræver en tom constructor for at kunne hente data fra databasen
    public Equipment() {
    }

    // Constructor til når du skal oprette nyt udstyr i din kode
    public Equipment(String name, String description, boolean operational) {
        this.name = name;
        this.description = description;
        this.operational = operational;
    }

    // --- GETTERS & SETTERS ---
    // De er nødvendige for at Spring kan læse og skrive til felterne

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOperational() {
        return operational;
    }

    public void setOperational(boolean operational) {
        this.operational = operational;
    }
}