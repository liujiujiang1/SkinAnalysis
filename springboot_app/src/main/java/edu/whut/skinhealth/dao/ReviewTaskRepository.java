package edu.whut.skinhealth.dao;

import edu.whut.skinhealth.entity.ReviewTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewTaskRepository extends JpaRepository<ReviewTask, Long> {
    List<ReviewTask> findAllByOrderByCreatedTimeDesc();

    Optional<ReviewTask> findTopByRecordIdAndTaskType(Long recordId, String taskType);

    Optional<ReviewTask> findTopByFeedbackIdAndTaskType(Long feedbackId, String taskType);
}
