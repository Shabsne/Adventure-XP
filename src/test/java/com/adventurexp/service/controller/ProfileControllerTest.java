package com.adventurexp.service.controller;

import com.adventurexp.model.Profile;
import com.adventurexp.model.Role;
import com.adventurexp.repository.ProfileRepository;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void registerUser() throws Exception {

        String json = """
        {
          "name":"JUnit",
          "role":"Customer",
          "birthDate":"2000-01-01",
          "mail":"junit@test.dk",
          "password":"1234"
        }
        """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void registerDuplicateEmail() throws Exception {

        Profile profile = new Profile(
                "Test",
                Role.Customer,
                LocalDate.of(2000,1,1),
                "test@test.dk",
                "1234"
        );

        profileRepository.save(profile);

        String json = """
    {
      "name":"JUnit",
      "role":"Custommer",
      "birthDate":"2000-01-01",
      "mail":"test@test.dk",
      "password":"1234"
    }
    """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }
}
