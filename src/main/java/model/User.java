package model;

public abstract class User {
    private final String id;
    private final Role role;
    private String username;
    private String passwordHash;
    private boolean temporaryPassword;
    private boolean active = true;

    protected User(String id, String username, String passwordHash, boolean temporaryPassword, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.temporaryPassword = temporaryPassword;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(boolean temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public abstract String getDisplayName();
}
