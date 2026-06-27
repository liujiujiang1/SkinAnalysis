package edu.whut.skinhealth.dao;

import edu.whut.skinhealth.entity.LesionProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LesionProfileRepository extends JpaRepository<LesionProfile, Long> {
    List<LesionProfile> findByUserUsernameOrderByUpdatedTimeDesc(String username);
}
