package com.adventurexp.service;

import com.adventurexp.model.Profile;
import com.adventurexp.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile login(String mail, String password) {

        Profile profile = profileRepository.findByMail(mail);

        if (profile != null && profile.getPassword().equals(password)) {
            return profile;
        }

        return null;
    }

    public Profile readProfile(int id) {
        return profileRepository.findById(id).orElse(null);
    }

    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    public boolean existsByMail(String mail) {
        return profileRepository.existsByMail(mail);
    }

}
