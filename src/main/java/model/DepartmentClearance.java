package model;

import java.time.LocalDateTime;

public class DepartmentClearance {
    private final String id;
    private final String clearanceRecordId;
    private final String departmentId;
    private final String departmentNameSnapshot;
    private final boolean requiredSnapshot;
    private ClearanceStatus status = ClearanceStatus.PENDING;
    private String remarks = "";
    private String reviewedByUserId;
    private LocalDateTime reviewedAt;

    public DepartmentClearance(String id, String clearanceRecordId, Department department) {
        this.id = id;
        this.clearanceRecordId = clearanceRecordId;
        this.departmentId = department.getId();
        this.departmentNameSnapshot = department.getName();
        this.requiredSnapshot = department.isRequired();
    }

    public String getId() {
        return id;
    }

    public String getClearanceRecordId() {
        return clearanceRecordId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentNameSnapshot() {
        return departmentNameSnapshot;
    }

    public boolean isRequiredSnapshot() {
        return requiredSnapshot;
    }

    public ClearanceStatus getStatus() {
        return status;
    }

    public void setStatus(ClearanceStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? "" : remarks;
    }

    public String getReviewedByUserId() {
        return reviewedByUserId;
    }

    public void setReviewedByUserId(String reviewedByUserId) {
        this.reviewedByUserId = reviewedByUserId;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
