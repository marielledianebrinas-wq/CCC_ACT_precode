package service;

import data.SessionData;
import java.util.ArrayList;
import java.util.List;
import model.*;
import util.PasswordUtil;
import util.ValidationUtil;

public class UserService {
    private final SessionData data;

    public UserService(SessionData data) {
        this.data = data;
    }

    public List<User> allUsers() {
        return new ArrayList<User>(data.getUsers().values());
    }

    public List<Student> students() {
        List<Student> result = new ArrayList<Student>();
        for (User user : data.getUsers().values()) {
            if (user instanceof Student) {
                result.add((Student) user);
            }
        }
        return result;
    }

    public List<DepartmentStaff> staffUsers() {
        List<DepartmentStaff> result = new ArrayList<DepartmentStaff>();
        for (User user : data.getUsers().values()) {
            if (user instanceof DepartmentStaff) {
                result.add((DepartmentStaff) user);
            }
        }
        return result;
    }

    public User findByUsername(String username) {
        for (User user : data.getUsers().values()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public User getById(String id) {
        return data.getUsers().get(id);
    }

    public Student registerStudent(String username, String password, String studentId, String name,
                                   String program, String yearLevel, String contact, String email, String address) {
        Student student = createStudentAccount(username, password, studentId, name, program, yearLevel, contact, email, address);
        data.addAudit(student, "Student self-registration", student.getId(), "", username, "Account uses temporary password.");
        return student;
    }

    public Student createStudent(AdminUser admin, String username, String tempPassword, String studentId, String name,
                                 String program, String yearLevel, String contact, String email, String address) {
        requireAdmin(admin);
        Student student = createStudentAccount(username, tempPassword, studentId, name, program, yearLevel, contact, email, address);
        data.addAudit(admin, "Student account registered", student.getId(), "", username, "Account uses temporary password.");
        return student;
    }

    private Student createStudentAccount(String username, String password, String studentId, String name,
                                         String program, String yearLevel, String contact, String email, String address) {
        ValidationUtil.requireText(username, "Username");
        ValidationUtil.requireText(password, "Password");
        ValidationUtil.requireText(studentId, "Student ID");
        ValidationUtil.requireText(name, "Displayed name");
        ValidationUtil.requireText(program, "Program");
        ValidationUtil.requireText(yearLevel, "Year level");
        ValidationUtil.requireEmail(email);
        ensureUniqueUsername(username);
        Student student = new Student(data.nextUserId(), username.trim(), PasswordUtil.hashPassword(password), true,
                studentId.trim(), name.trim(), program.trim(), yearLevel.trim(), contact.trim(), email.trim(), address.trim());
        data.getUsers().put(student.getId(), student);
        return student;
    }

    public DepartmentStaff createStaff(AdminUser admin, String username, String tempPassword, String staffName, List<String> departmentIds) {
        requireAdmin(admin);
        ValidationUtil.requireText(username, "Username");
        ValidationUtil.requireText(tempPassword, "Temporary password");
        ValidationUtil.requireText(staffName, "Staff name");
        if (departmentIds == null || departmentIds.isEmpty()) {
            throw new IllegalArgumentException("Assign at least one department.");
        }
        ensureUniqueUsername(username);
        DepartmentStaff staff = new DepartmentStaff(data.nextUserId(), username.trim(), PasswordUtil.hashPassword(tempPassword), true, staffName.trim());
        for (String id : departmentIds) {
            if (!data.getDepartments().containsKey(id)) {
                throw new IllegalArgumentException("Invalid department assignment.");
            }
            staff.getAssignedDepartmentIds().add(id);
        }
        data.getUsers().put(staff.getId(), staff);
        data.addAudit(admin, "Staff account created", staff.getId(), "", staff.getUsername(), "Assigned departments: " + staff.getAssignedDepartmentIds());
        return staff;
    }

    public void setActive(AdminUser admin, User target, boolean active) {
        requireAdmin(admin);
        if (target == null) {
            throw new IllegalArgumentException("Select an account.");
        }
        if (!active && target.getRole() == Role.ADMIN && activeAdminCount() <= 1) {
            throw new IllegalArgumentException("Cannot deactivate the only active administrator.");
        }
        boolean previous = target.isActive();
        target.setActive(active);
        data.addAudit(admin, "Account activation changed", target.getId(), String.valueOf(previous), String.valueOf(active), "");
    }

    public void resetPassword(AdminUser admin, User target, String newTempPassword) {
        requireAdmin(admin);
        if (target == null) {
            throw new IllegalArgumentException("Select an account.");
        }
        ValidationUtil.requireText(newTempPassword, "Temporary password");
        target.setPasswordHash(PasswordUtil.hashPassword(newTempPassword));
        target.setTemporaryPassword(true);
        data.addAudit(admin, "Administrator password recovery", target.getId(), "", "Temporary password issued", "Password value not logged.");
    }

    public void updateStudentProfile(Student student, String contact, String email, String address) {
        ValidationUtil.requireEmail(email);
        String previous = student.getContactNumber() + " / " + student.getEmail() + " / " + student.getHomeAddress();
        student.setContactNumber(contact == null ? "" : contact.trim());
        student.setEmail(email.trim());
        student.setHomeAddress(address == null ? "" : address.trim());
        String next = student.getContactNumber() + " / " + student.getEmail() + " / " + student.getHomeAddress();
        data.addAudit(student, "Student contact profile updated", student.getId(), previous, next, "");
    }

    public void updateStudentAuthority(AdminUser admin, Student student, String studentId, String name, String program, String yearLevel) {
        requireAdmin(admin);
        ValidationUtil.requireText(studentId, "Student ID");
        ValidationUtil.requireText(name, "Displayed name");
        ValidationUtil.requireText(program, "Program");
        ValidationUtil.requireText(yearLevel, "Year level");
        String previous = student.getStudentId() + " / " + student.getDisplayName() + " / " + student.getProgram() + " / " + student.getYearLevel();
        student.setStudentId(studentId.trim());
        student.setDisplayedName(name.trim());
        student.setProgram(program.trim());
        student.setYearLevel(yearLevel.trim());
        String next = student.getStudentId() + " / " + student.getDisplayName() + " / " + student.getProgram() + " / " + student.getYearLevel();
        data.addAudit(admin, "Authoritative student profile updated", student.getId(), previous, next, "");
    }

    private void ensureUniqueUsername(String username) {
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }
    }

    private int activeAdminCount() {
        int count = 0;
        for (User user : data.getUsers().values()) {
            if (user.getRole() == Role.ADMIN && user.isActive()) {
                count++;
            }
        }
        return count;
    }

    private void requireAdmin(User user) {
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new SecurityException("Administrator access is required.");
        }
    }
}
