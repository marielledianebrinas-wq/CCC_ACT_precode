package tests;

import data.SessionData;
import java.util.Arrays;
import java.util.List;
import model.*;
import service.AppServices;
import util.PasswordUtil;

public class BusinessLogicTest {
    private int passed = 0;
    private int failed = 0;

    public static void main(String[] args) {
        BusinessLogicTest test = new BusinessLogicTest();
        test.run();
    }

    private void run() {
        test("Password verification and first-login enforcement", this::passwordAndFirstLogin);
        test("Duplicate username rejection", this::duplicateUsername);
        test("Role and department access restrictions", this::departmentAccess);
        test("Clearance generation and duplicate prevention", this::generationDuplicate);
        test("Correct progress and optional department behavior", this::progressRules);
        test("Flagged requests return to Pending", this::flaggedReReview);
        test("Name-change approval and rejection", this::nameChanges);
        test("Audit entries are recorded", this::auditEntries);
        test("Semester changes and historical records", this::semesterHistorical);
        test("Session reset creates fresh demo state", this::sessionReset);
        System.out.println("Tests passed: " + passed + ", failed: " + failed);
        if (failed > 0) {
            throw new AssertionError("Some tests failed.");
        }
    }

    private void passwordAndFirstLogin() {
        AppServices s = newServices();
        User admin = s.auth.login("admin", "Temp@123");
        check(admin.isTemporaryPassword(), "demo admin starts with temporary password");
        s.auth.changeTemporaryPassword(admin, "NewPass123");
        check(!admin.isTemporaryPassword(), "temporary flag cleared");
        check(PasswordUtil.verifyPassword("NewPass123", admin.getPasswordHash()), "new password verifies");
    }

    private void duplicateUsername() {
        AppServices s = newServices();
        expect(() -> s.users.registerStudent("student.luna", "Password1", "X", "X", "BS", "1st", "1", "x@y.com", "X"));
    }

    private void departmentAccess() {
        AppServices s = newServices();
        AdminUser admin = admin(s);
        s.auth.changeTemporaryPassword(admin, "NewPass123");
        Student student = s.users.students().get(0);
        ClearanceRecord record = s.clearances.generateForStudents(admin, Arrays.asList(student)).get(0);
        DepartmentStaff staff = staffByUsername(s, "staff.president");
        DepartmentClearance treasurerEntry = record.getEntries().get(0);
        expect(() -> s.clearances.approve(staff, treasurerEntry.getId(), ""));
    }

    private void generationDuplicate() {
        AppServices s = newServices();
        AdminUser admin = admin(s);
        s.auth.changeTemporaryPassword(admin, "NewPass123");
        Student student = s.users.students().get(0);
        s.clearances.generateForStudents(admin, Arrays.asList(student));
        expect(() -> s.clearances.generateForStudents(admin, Arrays.asList(student)));
    }

    private void progressRules() {
        AppServices s = newServices();
        AdminUser admin = admin(s);
        s.auth.changeTemporaryPassword(admin, "NewPass123");
        Student student = s.users.students().get(0);
        ClearanceRecord record = s.clearances.generateForStudents(admin, Arrays.asList(student)).get(0);
        check(record.getRequiredCount() == 4, "four required default departments");
        check(record.getProgressPercent() == 0, "starts at zero");
        approveAllRequired(s, record);
        check(record.getProgressPercent() == 100, "required approvals complete progress");
        check("Completed".equals(record.getOverallStatus()), "optional entries do not block completion");
    }

    private void flaggedReReview() {
        AppServices s = newServices();
        AdminUser admin = admin(s);
        s.auth.changeTemporaryPassword(admin, "NewPass123");
        Student student = s.users.students().get(0);
        ClearanceRecord record = s.clearances.generateForStudents(admin, Arrays.asList(student)).get(0);
        DepartmentClearance entry = record.getEntries().get(0);
        DepartmentStaff staff = staffForDepartment(s, entry.getDepartmentId());
        s.clearances.flag(staff, entry.getId(), "Need receipt");
        check(entry.getStatus() == ClearanceStatus.FLAGGED, "entry flagged");
        s.clearances.markIssueAddressed(student, entry.getId());
        check(entry.getStatus() == ClearanceStatus.PENDING, "entry returned to pending");
    }

    private void nameChanges() {
        AppServices s = newServices();
        AdminUser admin = admin(s);
        Student student = s.users.students().get(0);
        NameChangeRequest approved = s.names.submit(student, "Luna R. Reyes", "Correct school display");
        s.names.decide(admin, approved, true, "Verified");
        check("Luna R. Reyes".equals(student.getDisplayName()), "approval changes display name");
        NameChangeRequest rejected = s.names.submit(student, "Wrong Name", "Test rejection");
        s.names.decide(admin, rejected, false, "Not verified");
        check(!"Wrong Name".equals(student.getDisplayName()), "rejection does not change display name");
    }

    private void auditEntries() {
        AppServices s = newServices();
        int before = s.data.getAuditEntries().size();
        s.auth.login("admin", "Temp@123");
        check(s.data.getAuditEntries().size() > before, "login audit recorded");
    }

    private void semesterHistorical() {
        AppServices s = newServices();
        AdminUser admin = admin(s);
        Student student = s.users.students().get(0);
        ClearanceRecord first = s.clearances.generateForStudents(admin, Arrays.asList(student)).get(0);
        s.departments.setActiveSemester(admin, "Second Semester", "2026\u20132027");
        ClearanceRecord second = s.clearances.generateForStudents(admin, Arrays.asList(student)).get(0);
        check(!first.getTerm().equals(second.getTerm()), "different semester can create new record");
        check("First Semester".equals(first.getTerm()), "historical record retains original cycle");
    }

    private void sessionReset() {
        AppServices a = newServices();
        AppServices b = newServices();
        check(a.data.getClearanceRecords().isEmpty(), "fresh session has no clearances");
        check(b.data.getClearanceRecords().isEmpty(), "second fresh session has no clearances");
        check(a.users.allUsers().size() == b.users.allUsers().size(), "demo users reset consistently");
    }

    private void approveAllRequired(AppServices s, ClearanceRecord record) {
        for (DepartmentClearance entry : record.getEntries()) {
            if (entry.isRequiredSnapshot()) {
                s.clearances.approve(staffForDepartment(s, entry.getDepartmentId()), entry.getId(), "OK");
            }
        }
    }

    private DepartmentStaff staffForDepartment(AppServices s, String departmentId) {
        for (DepartmentStaff staff : s.users.staffUsers()) {
            if (staff.isAssignedTo(departmentId)) {
                return staff;
            }
        }
        throw new AssertionError("No staff for department " + departmentId);
    }

    private DepartmentStaff staffByUsername(AppServices s, String username) {
        return (DepartmentStaff) s.users.findByUsername(username);
    }

    private AdminUser admin(AppServices s) {
        return (AdminUser) s.users.findByUsername("admin");
    }

    private AppServices newServices() {
        return new AppServices(SessionData.createDemoData());
    }

    private void test(String name, Runnable runnable) {
        try {
            runnable.run();
            passed++;
            System.out.println("PASS - " + name);
        } catch (Throwable ex) {
            failed++;
            System.out.println("FAIL - " + name + ": " + ex.getMessage());
        }
    }

    private void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private void expect(Runnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException expected) {
            return;
        }
        throw new AssertionError("Expected an exception.");
    }
}
