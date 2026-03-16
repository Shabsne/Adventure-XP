package com.adventurexp.controller;

import com.adventurexp.DTO.CreateUserRequest;
import com.adventurexp.DTO.LoginRequest;
import com.adventurexp.model.Profile;
import com.adventurexp.model.Role;
import com.adventurexp.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }



    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Profile profile = profileService.login(
                loginRequest.getMail(),
                loginRequest.getPassword()
        );

        if (profile != null) {
            session.setAttribute("user", profile);

            return ResponseEntity.ok("Login succes");
        }

        return ResponseEntity.status(401).body("Wrong mail or password");
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @GetMapping("/me")
    public Profile getCurrentUser(HttpSession session) {
        return (Profile) session.getAttribute("user");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CreateUserRequest request, HttpSession session) {

        Profile currentUser = getCurrentUser(session);


        if (profileService.existsByMail(request.getMail())) {
            return ResponseEntity.status(409).body("Email er allerede i brug");
        }

        Role role;

        if (currentUser != null && currentUser.getRole() == Role.Admin && request.getRole() != null) {
            role = request.getRole();
        } else {
            role = Role.Customer;
        }



        profileService.createUser(request.getName(), request.getMail(), request.getPassword(), request.getBirthDate(), role);

        return ResponseEntity.ok("Bruger oprettet");
    }

    @GetMapping("/roles")
    public Role[] getRoles() {
        return Role.values();
    }
}
