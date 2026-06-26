package edu.whut.skinhealth.entity;

import java.sql.Timestamp;
import edu.whut.skinhealth.po.UserInfo;
import jakarta.persistence.*;
@Entity
@Table(name = "record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(targetEntity = UserInfo.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfo user;
    private String disease;
    private Timestamp time;
    private Double probability;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String topResults;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imageData;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String adviceBrief;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String adviceTreatment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
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
