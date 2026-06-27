package edu.whut.skinhealth.po;

public class FeedbackRequest {
    private Long recordId;
    private String username;
    private Boolean accurate;
    private String realDisease;
    private String comment;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAccurate() {
        return accurate;
    }

    public void setAccurate(Boolean accurate) {
        this.accurate = accurate;
    }

    public String getRealDisease() {
        return realDisease;
    }

    public void setRealDisease(String realDisease) {
        this.realDisease = realDisease;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
