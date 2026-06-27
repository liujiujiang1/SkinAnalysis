package edu.whut.skinhealth.config;

import edu.whut.skinhealth.dao.RecordRepository;
import edu.whut.skinhealth.service.DiseaseKnowledgeService;
import edu.whut.skinhealth.service.FeedbackService;
import edu.whut.skinhealth.service.RecordService;
import edu.whut.skinhealth.service.ReviewTaskService;
import edu.whut.skinhealth.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataNormalizationRunner implements ApplicationRunner {
    private final UserService userService;
    private final RecordRepository recordRepository;
    private final DiseaseKnowledgeService diseaseKnowledgeService;
    private final RecordService recordService;
    private final FeedbackService feedbackService;
    private final ReviewTaskService reviewTaskService;

    public DataNormalizationRunner(
            UserService userService,
            RecordRepository recordRepository,
            DiseaseKnowledgeService diseaseKnowledgeService,
            RecordService recordService,
            FeedbackService feedbackService,
            ReviewTaskService reviewTaskService) {
        this.userService = userService;
        this.recordRepository = recordRepository;
        this.diseaseKnowledgeService = diseaseKnowledgeService;
        this.recordService = recordService;
        this.feedbackService = feedbackService;
        this.reviewTaskService = reviewTaskService;
    }

    @Override
    public void run(ApplicationArguments args) {
        userService.normalizeExistingUsers();
        recordRepository.deleteRecordsWithMissingUser();
        diseaseKnowledgeService.seedDefaultsIfEmpty();
        reviewTaskService.backfillMissingTasks(recordService.getAllRecords(), feedbackService.getAllFeedback());
    }
}
