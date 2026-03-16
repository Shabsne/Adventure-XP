package com.adventurexp.service;

import com.adventurexp.model.Profile;
import com.adventurexp.model.Role;
import com.adventurexp.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProfileServiceTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void testRegisterUser() {

        Profile profile = new Profile(
                "Test User",
                Role.Customer,
                LocalDate.of(2000, 1, 1),
                "test@test.dk",
                "1234"
        );

        profileRepository.save(profile);

        Optional<Profile> result =
                Optional.ofNullable(profileRepository.findByMail("test@test.dk"));

        assertTrue(result.isPresent());
    }

    @Test
    void testLoginFail() {

        Profile result = profileService.login("wrong@test.dk", "1234");

        assertNull(result);
    }
}
