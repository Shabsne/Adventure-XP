package com.adventurexp.controller;

import com.adventurexp.DTO.LoginRequest;
import com.adventurexp.model.Profile;
import com.adventurexp.service.ProfileService;
import jakarta.servlet.http.HttpSession;
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
    public String login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Profile profile = profileService.login(
                loginRequest.getMail(),
                loginRequest.getPassword()
        );

        if (profile != null) {
            session.setAttribute("user", profile);

            return "success";
        }

        return "error";
    }

    @PostMapping("logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @GetMapping("/me")
    public Profile getCurrentUser(HttpSession session) {
        return (Profile) session.getAttribute("user");
    }
}
