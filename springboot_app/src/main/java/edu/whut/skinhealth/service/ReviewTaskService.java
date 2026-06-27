package edu.whut.skinhealth.service;

import edu.whut.skinhealth.dao.ReviewTaskRepository;
import edu.whut.skinhealth.entity.Feedback;
import edu.whut.skinhealth.entity.Record;
import edu.whut.skinhealth.entity.ReviewTask;
import edu.whut.skinhealth.po.UserInfo;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewTaskService {
    private final ReviewTaskRepository reviewTaskRepository;

    public ReviewTaskService(ReviewTaskRepository reviewTaskRepository) {
        this.reviewTaskRepository = reviewTaskRepository;
    }

    public List<ReviewTask> getAll() {
        return reviewTaskRepository.findAllByOrderByCreatedTimeDesc();
    }

    public Optional<ReviewTask> getById(Long id) {
        return reviewTaskRepository.findById(id);
    }

    public ReviewTask save(ReviewTask reviewTask) {
        Timestamp now = now();
        if (reviewTask.getCreatedTime() == null) {
            reviewTask.setCreatedTime(now);
        }
        reviewTask.setUpdatedTime(now);
        if (reviewTask.getStatus() == null || reviewTask.getStatus().isBlank()) {
            reviewTask.setStatus("待处理");
        }
        if (reviewTask.getTrainingCandidate() == null) {
            reviewTask.setTrainingCandidate(false);
        }
        return reviewTaskRepository.save(reviewTask);
    }

    public void createForRecordIfNeeded(Record record) {
        if (record == null || record.getId() == null) {
            return;
        }
        Double probability = record.getProbability() == null ? 0.0 : record.getProbability();
        String riskLevel = record.getRiskLevel() == null ? "" : record.getRiskLevel();
        if (isHighRisk(riskLevel)) {
            createRecordTask(record, "高风险诊断", "系统识别为高风险或中高风险，建议管理员复核诊断记录和就医提醒。");
        }
        if (probability < 60.0) {
            createRecordTask(record, "低置信度诊断", "模型首要结果置信度低于 60%，建议检查图片质量和 Top3 分布。");
        }
    }

    public void createForFeedbackIfNeeded(Feedback feedback) {
        if (feedback == null || feedback.getId() == null || Boolean.TRUE.equals(feedback.getAccurate())) {
            return;
        }
        Optional<ReviewTask> existed = reviewTaskRepository.findTopByFeedbackIdAndTaskType(feedback.getId(), "用户反馈复核");
        if (existed.isPresent()) {
            return;
        }
        ReviewTask task = new ReviewTask();
        task.setFeedback(feedback);
        task.setRecord(feedback.getRecord());
        task.setTaskType("用户反馈复核");
        task.setUsername(feedback.getUsername());
        task.setDisease(feedback.getPredictedDisease());
        task.setRiskLevel(feedback.getRecord() == null ? null : feedback.getRecord().getRiskLevel());
        task.setReason("用户反馈诊断结果不准确，需复核真实诊断并判断是否纳入模型改进样本。");
        save(task);
    }

    private void createRecordTask(Record record, String taskType, String reason) {
        Optional<ReviewTask> existed = reviewTaskRepository.findTopByRecordIdAndTaskType(record.getId(), taskType);
        if (existed.isPresent()) {
            return;
        }
        ReviewTask task = new ReviewTask();
        UserInfo user = record.getUser();
        task.setRecord(record);
        task.setTaskType(taskType);
        task.setUsername(user == null ? null : user.getUsername());
        task.setDisease(record.getDisease());
        task.setRiskLevel(record.getRiskLevel());
        task.setReason(reason);
        save(task);
    }

    private boolean isHighRisk(String riskLevel) {
        return riskLevel.contains("高");
    }

    private Timestamp now() {
        return new Timestamp(new Date(System.currentTimeMillis()).getTime());
    }
}
