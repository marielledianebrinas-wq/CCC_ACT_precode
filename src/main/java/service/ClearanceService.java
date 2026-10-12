package service;

import data.SessionData;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.*;
import util.ValidationUtil;

public class ClearanceService {
    private final SessionData data;

    public ClearanceService(SessionData data) {
        this.data = data;
    }

    public List<ClearanceRecord> allRecords(AdminUser admin) {
        requireAdmin(admin);
        return new ArrayList<ClearanceRecord>(data.getClearanceRecords());
    }

    public List<ClearanceRecord> recordsForStudent(Student student) {
        List<ClearanceRecord> result = new ArrayList<ClearanceRecord>();
        for (ClearanceRecord record : data.getClearanceRecords()) {
            if (record.getStudentUserId().equals(student.getId())) {
                result.add(record);
            }
        }
        return result;
    }

    public List<DepartmentClearance> queueForStaff(DepartmentStaff staff, String departmentId, String statusFilter, String search) {
        List<DepartmentClearance> result = new ArrayList<DepartmentClearance>();
        for (ClearanceRecord record : data.getClearanceRecords()) {
            Student student = (Student) data.getUsers().get(record.getStudentUserId());
            for (DepartmentClearance entry : record.getEntries()) {
                if (!staff.isAssignedTo(entry.getDepartmentId())) {
                    continue;
                }
                if (departmentId != null && !departmentId.isEmpty() && !entry.getDepartmentId().equals(departmentId)) {
                    continue;
                }
                if (statusFilter != null && !statusFilter.isEmpty() && !entry.getStatus().name().equals(statusFilter)) {
                    continue;
                }
                String haystack = (student.getDisplayName() + " " + student.getStudentId() + " " + entry.getDepartmentNameSnapshot()).toLowerCase();
                if (search != null && !search.trim().isEmpty() && !haystack.contains(search.trim().toLowerCase())) {
                    continue;
                }
                result.add(entry);
            }
        }
        return result;
    }

    public List<ClearanceRecord> generateForStudents(AdminUser admin, List<Student> students) {
        requireAdmin(admin);
        if (students == null || students.isEmpty()) {
            throw new IllegalArgumentException("Select at least one student.");
        }
        List<Department> active = new ArrayList<Department>();
        int required = 0;
        for (Department department : data.getDepartments().values()) {
            if (department.isActive()) {
                active.add(department);
                if (department.isRequired()) {
                    required++;
                }
            }
        }
        if (active.isEmpty() || required == 0) {
            throw new IllegalArgumentException("At least one active required department is needed.");
        }
        List<ClearanceRecord> created = new ArrayList<ClearanceRecord>();
        for (Student student : students) {
            ensureNoDuplicate(student, data.getActiveSemester().getTerm(), data.getActiveSemester().getAcademicYear());
            ClearanceRecord record = new ClearanceRecord(data.nextClearanceId(), student.getId(),
                    data.getActiveSemester().getTerm(), data.getActiveSemester().getAcademicYear());
            for (Department department : active) {
                record.getEntries().add(new DepartmentClearance(data.nextDepartmentClearanceId(), record.getId(), department));
            }
            data.getClearanceRecords().add(record);
            created.add(record);
            data.addAudit(admin, "Clearance generated", record.getId() + ":" + student.getId(), "",
                    record.getTerm() + " " + record.getAcademicYear(), "Entries: " + record.getEntries().size());
        }
        return created;
    }

    public void approve(DepartmentStaff staff, String departmentClearanceId, String remarks) {
        DepartmentClearance entry = requireStaffAccess(staff, departmentClearanceId);
        if (entry.getStatus() != ClearanceStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be approved.");
        }
        String previous = entry.getStatus().getLabel();
        entry.setStatus(ClearanceStatus.APPROVED);
        entry.setRemarks(remarks);
        entry.setReviewedByUserId(staff.getId());
        entry.setReviewedAt(LocalDateTime.now());
        data.addAudit(staff, "Department clearance approved", reference(entry), previous, entry.getStatus().getLabel(), remarks);
    }

    public void flag(DepartmentStaff staff, String departmentClearanceId, String remarks) {
        DepartmentClearance entry = requireStaffAccess(staff, departmentClearanceId);
        if (entry.getStatus() != ClearanceStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be flagged.");
        }
        ValidationUtil.requireText(remarks, "Flag reason");
        String previous = entry.getStatus().getLabel();
        entry.setStatus(ClearanceStatus.FLAGGED);
        entry.setRemarks(remarks.trim());
        entry.setReviewedByUserId(staff.getId());
        entry.setReviewedAt(LocalDateTime.now());
        data.addAudit(staff, "Department clearance flagged", reference(entry), previous, entry.getStatus().getLabel(), remarks);
    }

    public void markIssueAddressed(Student student, String departmentClearanceId) {
        DepartmentClearance entry = findEntry(departmentClearanceId);
        ClearanceRecord record = findRecord(entry.getClearanceRecordId());
        if (!record.getStudentUserId().equals(student.getId())) {
            throw new SecurityException("Students can update only their own clearance entries.");
        }
        if (entry.getStatus() != ClearanceStatus.FLAGGED) {
            throw new IllegalArgumentException("Only flagged entries can be returned for re-review.");
        }
        String previous = entry.getStatus().getLabel();
        entry.setStatus(ClearanceStatus.PENDING);
        entry.setRemarks("Student marked the issue as addressed. Previous remark: " + entry.getRemarks());
        entry.setReviewedByUserId(null);
        entry.setReviewedAt(null);
        data.addAudit(student, "Flagged issue addressed", reference(entry) + ":" + student.getId(), previous, entry.getStatus().getLabel(), entry.getRemarks());
    }

    public ClearanceRecord findRecord(String id) {
        for (ClearanceRecord record : data.getClearanceRecords()) {
            if (record.getId().equals(id)) {
                return record;
            }
        }
        throw new IllegalArgumentException("Clearance record not found.");
    }

    public DepartmentClearance findEntry(String id) {
        for (ClearanceRecord record : data.getClearanceRecords()) {
            for (DepartmentClearance entry : record.getEntries()) {
                if (entry.getId().equals(id)) {
                    return entry;
                }
            }
        }
        throw new IllegalArgumentException("Department clearance not found.");
    }

    public Student studentFor(DepartmentClearance entry) {
        ClearanceRecord record = findRecord(entry.getClearanceRecordId());
        return (Student) data.getUsers().get(record.getStudentUserId());
    }

    private void ensureNoDuplicate(Student student, String term, String academicYear) {
        for (ClearanceRecord record : data.getClearanceRecords()) {
            if (record.getStudentUserId().equals(student.getId()) &&
                    record.getTerm().equals(term) &&
                    record.getAcademicYear().equals(academicYear)) {
                throw new IllegalArgumentException("Duplicate clearance for " + student.getDisplayName() + " and " + term + " " + academicYear + ".");
            }
        }
    }

    private DepartmentClearance requireStaffAccess(DepartmentStaff staff, String departmentClearanceId) {
        if (staff == null || staff.getRole() != Role.STAFF) {
            throw new SecurityException("Department staff access is required.");
        }
        DepartmentClearance entry = findEntry(departmentClearanceId);
        if (!staff.isAssignedTo(entry.getDepartmentId())) {
            throw new SecurityException("This staff account is not assigned to that department.");
        }
        return entry;
    }

    private String reference(DepartmentClearance entry) {
        return entry.getClearanceRecordId() + ":" + entry.getId() + ":" + entry.getDepartmentId();
    }

    private void requireAdmin(User user) {
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new SecurityException("Administrator access is required.");
        }
    }
}
