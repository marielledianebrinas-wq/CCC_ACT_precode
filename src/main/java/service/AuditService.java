package service;

import data.SessionData;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class AuditService {
    private final SessionData data;

    public AuditService(SessionData data) {
        this.data = data;
    }

    public void record(User actor, String action, String recordRef, String previous, String next, String remarks) {
        data.addAudit(actor, action, recordRef, previous, next, remarks);
    }

    public List<AuditEntry> all(User actor) {
        requireAdmin(actor);
        return new ArrayList<AuditEntry>(data.getAuditEntries());
    }

    public List<AuditEntry> forStudent(Student student) {
        List<AuditEntry> result = new ArrayList<AuditEntry>();
        for (AuditEntry entry : data.getAuditEntries()) {
            if (entry.getActingUserId().equals(student.getId()) || entry.getRecordReference().contains(student.getId())) {
                result.add(entry);
            }
        }
        return result;
    }

    public List<AuditEntry> forStaff(DepartmentStaff staff) {
        List<AuditEntry> result = new ArrayList<AuditEntry>();
        for (AuditEntry entry : data.getAuditEntries()) {
            String ref = entry.getRecordReference();
            for (String deptId : staff.getAssignedDepartmentIds()) {
                if (ref.contains(deptId) || entry.getActingUserId().equals(staff.getId())) {
                    result.add(entry);
                    break;
                }
            }
        }
        return result;
    }

    private void requireAdmin(User actor) {
        if (actor == null || actor.getRole() != Role.ADMIN) {
            throw new SecurityException("Administrator access is required.");
        }
    }
}
