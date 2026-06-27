package edu.whut.skinhealth.config;

import edu.whut.skinhealth.dao.RecordRepository;
import edu.whut.skinhealth.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataNormalizationRunner implements ApplicationRunner {
    private final UserService userService;
    private final RecordRepository recordRepository;

    public DataNormalizationRunner(UserService userService, RecordRepository recordRepository) {
        this.userService = userService;
        this.recordRepository = recordRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        userService.normalizeExistingUsers();
        recordRepository.deleteRecordsWithMissingUser();
    }
}
