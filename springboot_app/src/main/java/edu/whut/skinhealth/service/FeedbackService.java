package edu.whut.skinhealth.service;

import edu.whut.skinhealth.dao.FeedbackRepository;
import edu.whut.skinhealth.entity.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllByOrderByFeedbackTimeDesc();
    }

    public List<Feedback> getFeedbackByUsername(String username) {
        return feedbackRepository.findByUsernameOrderByFeedbackTimeDesc(username);
    }

    public List<Feedback> getFeedbackByRecordId(Long recordId) {
        return feedbackRepository.findByRecordIdOrderByFeedbackTimeDesc(recordId);
    }

    public Optional<Feedback> getLatestFeedback(Long recordId, String username) {
        return feedbackRepository.findTopByRecordIdAndUsernameOrderByFeedbackTimeDesc(recordId, username);
    }

    public Optional<Feedback> getById(Long id) {
        return feedbackRepository.findById(id);
    }
}
