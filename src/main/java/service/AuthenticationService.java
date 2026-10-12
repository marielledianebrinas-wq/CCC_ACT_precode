package service;

import data.SessionData;
import model.User;
import util.PasswordUtil;
import util.ValidationUtil;

public class AuthenticationService {
    private final SessionData data;
    private final UserService userService;

    public AuthenticationService(SessionData data, UserService userService) {
        this.data = data;
        this.userService = userService;
    }

    public User login(String username, String password) {
        ValidationUtil.requireText(username, "Username");
        ValidationUtil.requireText(password, "Password");
        User user = userService.findByUsername(username.trim());
        if (user == null || !user.isActive() || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            data.addAudit(user, "Login failed", username, "", "", "Invalid username, password, or inactive account.");
            throw new IllegalArgumentException("Invalid username or password.");
        }
        data.addAudit(user, "Login success", user.getId(), "", "", "");
        return user;
    }

    public void changePassword(User user, String currentPassword, String newPassword) {
        if (user == null) {
            throw new SecurityException("Sign in first.");
        }
        ValidationUtil.requireText(currentPassword, "Current password");
        if (!PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        applyNewPassword(user, newPassword);
        data.addAudit(user, "Password changed", user.getId(), "", "Updated", "Password value not logged.");
    }

    public void changeTemporaryPassword(User user, String newPassword) {
        if (user == null) {
            throw new SecurityException("Sign in first.");
        }
        if (!user.isTemporaryPassword()) {
            throw new IllegalArgumentException("This account is not waiting for first-login password change.");
        }
        applyNewPassword(user, newPassword);
        data.addAudit(user, "Temporary password changed", user.getId(), "", "Permanent password set", "Password value not logged.");
    }

    private void applyNewPassword(User user, String newPassword) {
        ValidationUtil.requireText(newPassword, "New password");
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        user.setTemporaryPassword(false);
    }
}
