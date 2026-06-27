package edu.whut.skinhealth.service;

import edu.whut.skinhealth.dao.LesionProfileRepository;
import edu.whut.skinhealth.entity.LesionProfile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LesionProfileService {
    private final LesionProfileRepository lesionProfileRepository;

    public LesionProfileService(LesionProfileRepository lesionProfileRepository) {
        this.lesionProfileRepository = lesionProfileRepository;
    }

    public LesionProfile save(LesionProfile lesionProfile) {
        return lesionProfileRepository.save(lesionProfile);
    }

    public Optional<LesionProfile> getById(Long id) {
        return lesionProfileRepository.findById(id);
    }

    public List<LesionProfile> getByUsername(String username) {
        return lesionProfileRepository.findByUserUsernameOrderByUpdatedTimeDesc(username);
    }
}
