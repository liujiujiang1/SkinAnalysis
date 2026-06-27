package edu.whut.skinhealth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "disease_knowledge")
public class DiseaseKnowledge {
    @Id
    @Column(length = 20)
    private String code;

    private String name;
    private String risk;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String intro;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String advice;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String cautions;
    private String source;
    private String version;
    private String editor;
    private String reviewStatus;
    private Timestamp updatedTime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getCautions() {
        return cautions;
    }

    public String getSource() {
        return source;
    }

    public String getVersion() {
        return version;
    }

    public String getEditor() {
        return editor;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    public void setCautions(String cautions) {
        this.cautions = cautions;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }
}
