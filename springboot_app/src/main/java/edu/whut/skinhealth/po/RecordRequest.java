package edu.whut.skinhealth.po;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
public class RecordRequest {
    private String username;

    private String disease;
    private Timestamp time;
    private Double probability;
    private String topResults;
    private String imageData;
    private String adviceBrief;
    private String adviceTreatment;


    public String getDisease() {
        return disease;
    }

    public Timestamp getTime() {
        return time;
    }

    public Double getProbability() {
        return probability;
    }

    public String getTopResults() {
        return topResults;
    }

    public String getImageData() {
        return imageData;
    }

    public String getAdviceBrief() {
        return adviceBrief;
    }

    public String getAdviceTreatment() {
        return adviceTreatment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public void setTopResults(String topResults) {
        this.topResults = topResults;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public void setAdviceBrief(String adviceBrief) {
        this.adviceBrief = adviceBrief;
    }

    public void setAdviceTreatment(String adviceTreatment) {
        this.adviceTreatment = adviceTreatment;
    }
}
