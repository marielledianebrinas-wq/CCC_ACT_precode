package model;

public class AdminUser extends User {
    private String adminName;

    public AdminUser(String id, String username, String passwordHash, boolean temporaryPassword, String adminName) {
        super(id, username, passwordHash, temporaryPassword, Role.ADMIN);
        this.adminName = adminName;
    }

    @Override
    public String getDisplayName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}
