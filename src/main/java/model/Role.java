package model;

public enum Role {
    STUDENT("Student"),
    STAFF("Department Staff"),
    ADMIN("Administrator");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
