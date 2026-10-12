package model;

import java.time.LocalDateTime;

public class NameChangeRequest {
    private final String id;
    private final String studentUserId;
    private final String currentDisplayedName;
    private final String proposedDisplayedName;
    private final String reason;
    private RequestStatus status = RequestStatus.PENDING;
    private final LocalDateTime submittedAt;
    private String reviewingAdminId;
    private LocalDateTime decisionAt;
    private String decisionRemarks = "";

    public NameChangeRequest(String id, String studentUserId, String currentDisplayedName,
                             String proposedDisplayedName, String reason) {
        this.id = id;
        this.studentUserId = studentUserId;
        this.currentDisplayedName = currentDisplayedName;
        this.proposedDisplayedName = proposedDisplayedName;
        this.reason = reason;
        this.submittedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getStudentUserId() {
        return studentUserId;
    }

    public String getCurrentDisplayedName() {
        return currentDisplayedName;
    }

    public String getProposedDisplayedName() {
        return proposedDisplayedName;
    }

    public String getReason() {
        return reason;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public String getReviewingAdminId() {
        return reviewingAdminId;
    }

    public void setReviewingAdminId(String reviewingAdminId) {
        this.reviewingAdminId = reviewingAdminId;
    }

    public LocalDateTime getDecisionAt() {
        return decisionAt;
    }

    public void setDecisionAt(LocalDateTime decisionAt) {
        this.decisionAt = decisionAt;
    }

    public String getDecisionRemarks() {
        return decisionRemarks;
    }

    public void setDecisionRemarks(String decisionRemarks) {
        this.decisionRemarks = decisionRemarks == null ? "" : decisionRemarks;
    }
}
