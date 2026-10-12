package model;

import java.util.LinkedHashSet;
import java.util.Set;

public class DepartmentStaff extends User {
    private String staffName;
    private final Set<String> assignedDepartmentIds = new LinkedHashSet<String>();

    public DepartmentStaff(String id, String username, String passwordHash, boolean temporaryPassword, String staffName) {
        super(id, username, passwordHash, temporaryPassword, Role.STAFF);
        this.staffName = staffName;
    }

    @Override
    public String getDisplayName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Set<String> getAssignedDepartmentIds() {
        return assignedDepartmentIds;
    }

    public boolean isAssignedTo(String departmentId) {
        return assignedDepartmentIds.contains(departmentId);
    }
}
