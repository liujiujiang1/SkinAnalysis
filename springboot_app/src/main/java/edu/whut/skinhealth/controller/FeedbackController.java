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
import java.util.List;
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
        info.setFeedbackTime(feedback.getFeedbackTime());
        info.setComment(feedback.getComment());
        info.setRecordTime(record == null ? null : record.getTime());
        return info;
    }
}
