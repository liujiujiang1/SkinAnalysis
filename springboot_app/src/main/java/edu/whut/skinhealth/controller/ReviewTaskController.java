package edu.whut.skinhealth.controller;

import edu.whut.skinhealth.entity.ReviewTask;
import edu.whut.skinhealth.service.ReviewTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("review-task")
public class ReviewTaskController {
    private final ReviewTaskService reviewTaskService;

    public ReviewTaskController(ReviewTaskService reviewTaskService) {
        this.reviewTaskService = reviewTaskService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewTask>> queryAll() {
        return new ResponseEntity<>(reviewTaskService.getAll(), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<ReviewTask> update(@PathVariable("id") Long id, @RequestBody Map<String, Object> request) {
        Optional<ReviewTask> optional = reviewTaskService.getById(id);
        if (optional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ReviewTask task = optional.get();
        task.setStatus(stringValue(request.get("status"), task.getStatus()));
        task.setReviewNote(stringValue(request.get("reviewNote"), task.getReviewNote()));
        if (request.containsKey("trainingCandidate")) {
            task.setTrainingCandidate(Boolean.TRUE.equals(request.get("trainingCandidate")));
        }
        return new ResponseEntity<>(reviewTaskService.save(task), HttpStatus.OK);
    }

    private String stringValue(Object value, String fallback) {
        return value == null || String.valueOf(value).isBlank() ? fallback : String.valueOf(value);
    }
}
