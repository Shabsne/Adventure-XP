package com.adventurexp.service.controller;

import com.adventurexp.DTO.CreateUserRequest;
import com.adventurexp.model.Profile;
import com.adventurexp.model.Role;
import com.adventurexp.repository.ProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Autowired
    private ObjectMapper objectMapper;

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

        Profile existing = new Profile(
                "Test",
                Role.Customer,
                LocalDate.of(2000,1,1),
                "test@test.dk",
                "1234"
        );

        profileRepository.save(existing);

        CreateUserRequest request = new CreateUserRequest();
        request.setName("JUnit");
        request.setMail("test@test.dk");
        request.setPassword("1234");
        request.setBirthDate(LocalDate.of(2000,1,1));
        request.setRole(Role.Customer);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
