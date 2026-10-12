package service;

import data.SessionData;
import java.util.ArrayList;
import java.util.List;
import model.*;
import util.ValidationUtil;

public class DepartmentService {
    private final SessionData data;

    public DepartmentService(SessionData data) {
        this.data = data;
    }

    public List<Department> departments() {
        return new ArrayList<Department>(data.getDepartments().values());
    }

    public List<Department> activeDepartments() {
        List<Department> result = new ArrayList<Department>();
        for (Department department : data.getDepartments().values()) {
            if (department.isActive()) {
                result.add(department);
            }
        }
        return result;
    }

    public Department get(String id) {
        return data.getDepartments().get(id);
    }

    public void updateDepartment(AdminUser admin, Department department, boolean required, boolean active) {
        requireAdmin(admin);
        if (department == null) {
            throw new IllegalArgumentException("Select a department.");
        }
        String previous = "required=" + department.isRequired() + ", active=" + department.isActive();
        department.setRequired(required);
        department.setActive(active);
        String next = "required=" + required + ", active=" + active;
        data.addAudit(admin, "Department configuration changed", department.getId(), previous, next, department.getName());
    }

    public void setActiveSemester(AdminUser admin, String term, String academicYear) {
        requireAdmin(admin);
        if (!"First Semester".equals(term) && !"Second Semester".equals(term)) {
            throw new IllegalArgumentException("Only First Semester and Second Semester are supported.");
        }
        ValidationUtil.requireAcademicYear(academicYear);
        String previous = data.getActiveSemester().label();
        data.setActiveSemester(new Semester(term, academicYear));
        data.addAudit(admin, "Active semester changed", "SEMESTER", previous, data.getActiveSemester().label(), "");
    }

    private void requireAdmin(User user) {
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new SecurityException("Administrator access is required.");
        }
    }
}
