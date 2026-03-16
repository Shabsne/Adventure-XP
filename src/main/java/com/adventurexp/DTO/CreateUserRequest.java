package com.adventurexp.DTO;

import com.adventurexp.model.Role;

import java.time.LocalDate;

public class CreateUserRequest {

    private String name;
    private String mail;
    private String password;
    private LocalDate birthDate;
    private Role role;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

}
