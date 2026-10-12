package data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.*;
import util.PasswordUtil;

public class SessionData {
    private final Map<String, User> users = new LinkedHashMap<String, User>();
    private final Map<String, Department> departments = new LinkedHashMap<String, Department>();
    private final List<ClearanceRecord> clearanceRecords = new ArrayList<ClearanceRecord>();
    private final List<NameChangeRequest> nameChangeRequests = new ArrayList<NameChangeRequest>();
    private final List<AuditEntry> auditEntries = new ArrayList<AuditEntry>();
    private Semester activeSemester = new Semester("First Semester", "2026\u20132027");
    private int userSeq = 1;
    private int departmentSeq = 1;
    private int clearanceSeq = 1;
    private int departmentClearanceSeq = 1;
    private int nameRequestSeq = 1;
    private int auditSeq = 1;

    public static SessionData createDemoData() {
        SessionData data = new SessionData();
        data.seedDepartments();
        data.seedUsers();
        data.addAudit(null, "Session initialized", "DEMO", "", "Fresh demo data", "In-memory data resets on exit.");
        return data;
    }

    private void seedDepartments() {
        addDepartment("Department Treasurer", true);
        addDepartment("Department President", true);
        addDepartment("Curricular Organization President", true);
        addDepartment("Non Curricular Organization President", true);
        addDepartment("Department Adviser", false);
        addDepartment("Program Head", false);
        addDepartment("Guidance Councilor", false);
        addDepartment("Librarian", false);
        addDepartment("Registrar", false);
        addDepartment("Curricular Organization Adviser", false);
        addDepartment("Non Curricular Organization Adviser", false);
        addDepartment("Student Welfare and Alumni Affairs (SWAA) Director", false);
    }

    private void seedUsers() {
        AdminUser admin = new AdminUser(nextUserId(), "admin", PasswordUtil.hashPassword("Temp@123"), true, "Avery Santos");
        users.put(admin.getId(), admin);

        Student s1 = new Student(nextUserId(), "student.luna", PasswordUtil.hashPassword("Temp@123"), true,
                "CCC-2026-001", "Luna Reyes", "BS Information Systems", "2nd Year", "09171234567",
                "luna.reyes@example.edu", "Calapan City, Oriental Mindoro");
        Student s2 = new Student(nextUserId(), "student.mika", PasswordUtil.hashPassword("Temp@123"), true,
                "CCC-2026-002", "Mika Dela Cruz", "BS Computer Science", "3rd Year", "09181234567",
                "mika.delacruz@example.edu", "Naujan, Oriental Mindoro");
        Student s3 = new Student(nextUserId(), "student.noel", PasswordUtil.hashPassword("Temp@123"), true,
                "CCC-2026-003", "Noel Villanueva", "BS Information Technology", "4th Year", "09191234567",
                "noel.villanueva@example.edu", "Victoria, Oriental Mindoro");
        users.put(s1.getId(), s1);
        users.put(s2.getId(), s2);
        users.put(s3.getId(), s3);

        createSeedStaff("staff.treasurer", "Bianca Mariano", "Department Treasurer");
        createSeedStaff("staff.president", "Carlos Aquino", "Department President");
        createSeedStaff("staff.curricular", "Dana Soriano", "Curricular Organization President");
        createSeedStaff("staff.noncurricular", "Emilio Ramos", "Non Curricular Organization President");
        DepartmentStaff extra1 = createSeedStaff("staff.treasurer2", "Fatima Ong", "Department Treasurer");
        extra1.getAssignedDepartmentIds().add(findDepartmentByName("Department President").getId());
        DepartmentStaff extra2 = createSeedStaff("staff.curricular2", "Gino Mercado", "Curricular Organization President");
        extra2.getAssignedDepartmentIds().add(findDepartmentByName("Non Curricular Organization President").getId());
    }

    private DepartmentStaff createSeedStaff(String username, String name, String departmentName) {
        DepartmentStaff staff = new DepartmentStaff(nextUserId(), username, PasswordUtil.hashPassword("Temp@123"), true, name);
        staff.getAssignedDepartmentIds().add(findDepartmentByName(departmentName).getId());
        users.put(staff.getId(), staff);
        return staff;
    }

    public Department addDepartment(String name, boolean required) {
        Department department = new Department(nextDepartmentId(), name, required);
        departments.put(department.getId(), department);
        return department;
    }

    public AuditEntry addAudit(User actor, String action, String recordRef, String previous, String next, String remarks) {
        AuditEntry entry = new AuditEntry(nextAuditId(), action, actor, recordRef, previous, next, remarks);
        auditEntries.add(entry);
        return entry;
    }

    public String nextUserId() {
        return "U" + userSeq++;
    }

    public String nextDepartmentId() {
        return "D" + departmentSeq++;
    }

    public String nextClearanceId() {
        return "C" + clearanceSeq++;
    }

    public String nextDepartmentClearanceId() {
        return "DC" + departmentClearanceSeq++;
    }

    public String nextNameRequestId() {
        return "NR" + nameRequestSeq++;
    }

    public String nextAuditId() {
        return "A" + auditSeq++;
    }

    public Department findDepartmentByName(String name) {
        for (Department department : departments.values()) {
            if (department.getName().equalsIgnoreCase(name)) {
                return department;
            }
        }
        throw new IllegalArgumentException("Department not found: " + name);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<String, Department> getDepartments() {
        return departments;
    }

    public List<ClearanceRecord> getClearanceRecords() {
        return clearanceRecords;
    }

    public List<NameChangeRequest> getNameChangeRequests() {
        return nameChangeRequests;
    }

    public List<AuditEntry> getAuditEntries() {
        return auditEntries;
    }

    public Semester getActiveSemester() {
        return activeSemester;
    }

    public void setActiveSemester(Semester activeSemester) {
        this.activeSemester = activeSemester;
    }
}
