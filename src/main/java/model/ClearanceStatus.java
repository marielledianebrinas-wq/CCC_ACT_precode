package model;

public enum ClearanceStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    FLAGGED("Flagged");

    private final String label;

    ClearanceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
