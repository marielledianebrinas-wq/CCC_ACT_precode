package model;

import java.time.LocalDateTime;

public class AuditEntry {
    private final String id;
    private final String action;
    private final String actingUserId;
    private final String actingUsername;
    private final LocalDateTime timestamp;
    private final String recordReference;
    private final String previousValue;
    private final String newValue;
    private final String remarks;

    public AuditEntry(String id, String action, User actor, String recordReference,
                      String previousValue, String newValue, String remarks) {
        this.id = id;
        this.action = action;
        this.actingUserId = actor == null ? "SYSTEM" : actor.getId();
        this.actingUsername = actor == null ? "SYSTEM" : actor.getUsername();
        this.timestamp = LocalDateTime.now();
        this.recordReference = recordReference == null ? "" : recordReference;
        this.previousValue = previousValue == null ? "" : previousValue;
        this.newValue = newValue == null ? "" : newValue;
        this.remarks = remarks == null ? "" : remarks;
    }

    public String getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getActingUserId() {
        return actingUserId;
    }

    public String getActingUsername() {
        return actingUsername;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getRecordReference() {
        return recordReference;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getRemarks() {
        return remarks;
    }
}
