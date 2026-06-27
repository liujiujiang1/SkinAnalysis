package edu.whut.skinhealth.dao;

import edu.whut.skinhealth.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findAllByOrderByFeedbackTimeDesc();

    List<Feedback> findByUsernameOrderByFeedbackTimeDesc(String username);

    List<Feedback> findByRecordIdOrderByFeedbackTimeDesc(Long recordId);

    Optional<Feedback> findTopByRecordIdAndUsernameOrderByFeedbackTimeDesc(Long recordId, String username);
}
