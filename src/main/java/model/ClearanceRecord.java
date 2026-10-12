package model;

import java.util.ArrayList;
import java.util.List;

public class ClearanceRecord {
    private final String id;
    private final String studentUserId;
    private final String term;
    private final String academicYear;
    private final List<DepartmentClearance> entries = new ArrayList<DepartmentClearance>();

    public ClearanceRecord(String id, String studentUserId, String term, String academicYear) {
        this.id = id;
        this.studentUserId = studentUserId;
        this.term = term;
        this.academicYear = academicYear;
    }

    public String getId() {
        return id;
    }

    public String getStudentUserId() {
        return studentUserId;
    }

    public String getTerm() {
        return term;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public List<DepartmentClearance> getEntries() {
        return entries;
    }

    public int getRequiredCount() {
        int count = 0;
        for (DepartmentClearance entry : entries) {
            if (entry.isRequiredSnapshot()) {
                count++;
            }
        }
        return count;
    }

    public int getApprovedRequiredCount() {
        int count = 0;
        for (DepartmentClearance entry : entries) {
            if (entry.isRequiredSnapshot() && entry.getStatus() == ClearanceStatus.APPROVED) {
                count++;
            }
        }
        return count;
    }

    public int getProgressPercent() {
        int required = getRequiredCount();
        if (required == 0) {
            return 0;
        }
        return (int) Math.round((getApprovedRequiredCount() * 100.0) / required);
    }

    public String getOverallStatus() {
        if (getRequiredCount() == 0) {
            return "Invalid configuration";
        }
        return getApprovedRequiredCount() == getRequiredCount() ? "Completed" : "In Progress";
    }
}
