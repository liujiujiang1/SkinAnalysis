package edu.whut.skinhealth.controller;

import edu.whut.skinhealth.entity.Feedback;
import edu.whut.skinhealth.entity.Record;
import edu.whut.skinhealth.po.FeedbackInfo;
import edu.whut.skinhealth.po.FeedbackRequest;
import edu.whut.skinhealth.service.FeedbackService;
import edu.whut.skinhealth.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private RecordService recordService;

    @PostMapping
    public ResponseEntity<Feedback> addFeedback(@RequestBody FeedbackRequest request) {
        try {
            if (request.getRecordId() == null || request.getUsername() == null || request.getUsername().isBlank()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Optional<Record> recordOptional = recordService.getRecordById(request.getRecordId());
            if (recordOptional.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Record record = recordOptional.get();
            Feedback feedback = feedbackService.getLatestFeedback(request.getRecordId(), request.getUsername())
                    .orElseGet(Feedback::new);
            feedback.setRecord(record);
            feedback.setUsername(request.getUsername());
            feedback.setPredictedDisease(record.getDisease());
            feedback.setAccurate(request.getAccurate());
            feedback.setRealDisease(request.getRealDisease());
            feedback.setComment(request.getComment());
            if (feedback.getReviewStatus() == null || feedback.getReviewStatus().isBlank()) {
                feedback.setReviewStatus(Boolean.FALSE.equals(request.getAccurate()) ? "待复核" : "待审核");
            }
            feedback.setFeedbackTime(new Timestamp(new Date(System.currentTimeMillis()).getTime()));

            return new ResponseEntity<>(feedbackService.saveFeedback(feedback), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<FeedbackInfo>> queryAllFeedback() {
        try {
            return new ResponseEntity<>(toInfoList(feedbackService.getAllFeedback()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("user/{username}")
    public ResponseEntity<List<FeedbackInfo>> queryFeedbackByUsername(@PathVariable("username") String username) {
        try {
            return new ResponseEntity<>(toInfoList(feedbackService.getFeedbackByUsername(username)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("record/{recordId}")
    public ResponseEntity<List<FeedbackInfo>> queryFeedbackByRecordId(@PathVariable("recordId") Long recordId) {
        try {
            return new ResponseEntity<>(toInfoList(feedbackService.getFeedbackByRecordId(recordId)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}/review")
    public ResponseEntity<FeedbackInfo> reviewFeedback(@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
        try {
            Optional<Feedback> feedbackOptional = feedbackService.getById(id);
            if (feedbackOptional.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Feedback feedback = feedbackOptional.get();
            feedback.setReviewStatus(defaultIfBlank(request.get("reviewStatus"), "已审核"));
            feedback.setReviewNote(request.get("reviewNote"));
            feedback.setReviewTime(new Timestamp(new Date(System.currentTimeMillis()).getTime()));
            return new ResponseEntity<>(toInfo(feedbackService.saveFeedback(feedback)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("performance")
    public ResponseEntity<Map<String, Object>> queryModelPerformance() {
        try {
            List<FeedbackInfo> feedbackList = toInfoList(feedbackService.getAllFeedback());
            long total = feedbackList.size();
            long accurate = feedbackList.stream().filter(item -> Boolean.TRUE.equals(item.getAccurate())).count();
            long inaccurate = feedbackList.stream().filter(item -> Boolean.FALSE.equals(item.getAccurate())).count();

            Map<String, Map<String, Object>> byDisease = new HashMap<>();
            for (FeedbackInfo item : feedbackList) {
                String disease = defaultIfBlank(item.getPredictedDisease(), "未知");
                Map<String, Object> bucket = byDisease.computeIfAbsent(disease, key -> {
                    Map<String, Object> value = new HashMap<>();
                    value.put("disease", key);
                    value.put("total", 0L);
                    value.put("accurate", 0L);
                    value.put("inaccurate", 0L);
                    value.put("corrections", new HashMap<String, Long>());
                    return value;
                });

                bucket.put("total", (Long) bucket.get("total") + 1);
                if (Boolean.TRUE.equals(item.getAccurate())) {
                    bucket.put("accurate", (Long) bucket.get("accurate") + 1);
                } else if (Boolean.FALSE.equals(item.getAccurate())) {
                    bucket.put("inaccurate", (Long) bucket.get("inaccurate") + 1);
                    @SuppressWarnings("unchecked")
                    Map<String, Long> corrections = (Map<String, Long>) bucket.get("corrections");
                    String realDisease = defaultIfBlank(item.getRealDisease(), "未填写");
                    corrections.put(realDisease, corrections.getOrDefault(realDisease, 0L) + 1);
                }
            }

            for (Map<String, Object> bucket : byDisease.values()) {
                long diseaseTotal = (Long) bucket.get("total");
                long diseaseAccurate = (Long) bucket.get("accurate");
                bucket.put("accuracyRate", diseaseTotal == 0 ? 0.0 : Math.round(diseaseAccurate * 10000.0 / diseaseTotal) / 100.0);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("accurate", accurate);
            result.put("inaccurate", inaccurate);
            result.put("accuracyRate", total == 0 ? 0.0 : Math.round(accurate * 10000.0 / total) / 100.0);
            result.put("byDisease", byDisease.values());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<FeedbackInfo> toInfoList(List<Feedback> feedbackList) {
        return feedbackList.stream().map(this::toInfo).collect(Collectors.toList());
    }

    private FeedbackInfo toInfo(Feedback feedback) {
        FeedbackInfo info = new FeedbackInfo();
        Record record = feedback.getRecord();
        info.setId(feedback.getId());
        info.setRecordId(record == null ? null : record.getId());
        info.setUsername(feedback.getUsername());
        info.setPredictedDisease(feedback.getPredictedDisease());
        info.setAccurate(feedback.getAccurate());
        info.setRealDisease(feedback.getRealDisease());
        info.setReviewStatus(feedback.getReviewStatus());
        info.setReviewNote(feedback.getReviewNote());
        info.setFeedbackTime(feedback.getFeedbackTime());
        info.setReviewTime(feedback.getReviewTime());
        info.setComment(feedback.getComment());
        info.setRecordTime(record == null ? null : record.getTime());
        return info;
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
