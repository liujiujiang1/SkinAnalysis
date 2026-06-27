package edu.whut.skinhealth.controller;

import edu.whut.skinhealth.entity.DiseaseKnowledge;
import edu.whut.skinhealth.service.DiseaseKnowledgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("disease-knowledge")
public class DiseaseKnowledgeController {
    private final DiseaseKnowledgeService diseaseKnowledgeService;

    public DiseaseKnowledgeController(DiseaseKnowledgeService diseaseKnowledgeService) {
        this.diseaseKnowledgeService = diseaseKnowledgeService;
    }

    @GetMapping
    public ResponseEntity<List<DiseaseKnowledge>> queryAll() {
        return new ResponseEntity<>(diseaseKnowledgeService.getAll(), HttpStatus.OK);
    }

    @GetMapping("{code}")
    public ResponseEntity<DiseaseKnowledge> queryByCode(@PathVariable("code") String code) {
        return diseaseKnowledgeService.getByCode(code.toUpperCase())
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("{code}")
    public ResponseEntity<DiseaseKnowledge> update(@PathVariable("code") String code, @RequestBody DiseaseKnowledge request) {
        request.setCode(code);
        return new ResponseEntity<>(diseaseKnowledgeService.save(request), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DiseaseKnowledge> create(@RequestBody DiseaseKnowledge request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(diseaseKnowledgeService.save(request), HttpStatus.OK);
    }
}
