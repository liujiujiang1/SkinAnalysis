package edu.whut.skinhealth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

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

    public void setCautions(String cautions) {
        this.cautions = cautions;
    }
}
