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
    @ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity = UserInfo.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_record_user"))
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
    private String riskLevel;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String riskAdvice;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lesion_profile_id")
    private LesionProfile lesionProfile;

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

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRiskAdvice() {
        return riskAdvice;
    }

    public LesionProfile getLesionProfile() {
        return lesionProfile;
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

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setRiskAdvice(String riskAdvice) {
        this.riskAdvice = riskAdvice;
    }

    public void setLesionProfile(LesionProfile lesionProfile) {
        this.lesionProfile = lesionProfile;
    }
}
