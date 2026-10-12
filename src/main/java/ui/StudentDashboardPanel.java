package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import service.AppServices;
import util.FormatUtil;
import util.UiUtil;

public class StudentDashboardPanel extends JPanel {
    private final AppServices services;
    private final Student student;
    private final JTable clearanceTable = new JTable();
    private final JTable auditTable = new JTable();
    private final JLabel summary = UiUtil.smallTitle("");
    private final JTextField contact = UiUtil.field(20);
    private final JTextField email = UiUtil.field(20);
    private final JTextField address = UiUtil.field(20);

    public StudentDashboardPanel(AppServices services, Student student) {
        this.services = services;
        this.student = student;
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        UiUtil.styleTabs(tabs);
        tabs.add("Clearance", clearancePanel());
        tabs.add("Profile", profilePanel());
        tabs.add("Audit", auditPanel());
        add(tabs, BorderLayout.CENTER);
        refreshAll();
    }

    private JPanel clearancePanel() {
        JPanel page = UiUtil.page();
        JPanel top = UiUtil.card();
        top.setLayout(new BorderLayout(8, 8));
        top.add(UiUtil.title("Student Dashboard"), BorderLayout.NORTH);
        top.add(summary, BorderLayout.CENTER);
        JButton addressed = UiUtil.accentButton("Mark Selected Flagged Issue Addressed");
        addressed.addActionListener(e -> {
            try {
                String id = selectedId(clearanceTable, 0);
                services.clearances.markIssueAddressed(student, id);
                refreshAll();
                UiUtil.showInfo(this, "Entry returned to Pending for staff re-review.");
            } catch (Exception ex) {
                UiUtil.showError(this, ex);
            }
        });
        top.add(addressed, BorderLayout.SOUTH);
        UiUtil.styleTable(clearanceTable);
        page.add(top, BorderLayout.NORTH);
        page.add(new JScrollPane(clearanceTable), BorderLayout.CENTER);
        return page;
    }

    private JPanel profilePanel() {
        JPanel page = UiUtil.page();
        JPanel form = UiUtil.card();
        form.setLayout(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Student ID")); form.add(new JLabel(student.getStudentId()));
        form.add(new JLabel("Official displayed name")); form.add(new JLabel(student.getDisplayName()));
        form.add(new JLabel("Program")); form.add(new JLabel(student.getProgram()));
        form.add(new JLabel("Year level")); form.add(new JLabel(student.getYearLevel()));
        form.add(new JLabel("Contact number")); form.add(contact);
        form.add(new JLabel("Email")); form.add(email);
        form.add(new JLabel("Home address")); form.add(address);
        JButton save = UiUtil.accentButton("Save contact profile");
        save.addActionListener(e -> {
            try {
                services.users.updateStudentProfile(student, contact.getText(), email.getText(), address.getText());
                refreshAll();
                UiUtil.showInfo(this, "Profile updated.");
            } catch (Exception ex) {
                UiUtil.showError(this, ex);
            }
        });
        page.add(UiUtil.title("Profile Editor"), BorderLayout.NORTH);
        page.add(form, BorderLayout.CENTER);
        page.add(save, BorderLayout.SOUTH);
        return page;
    }

    private JPanel auditPanel() {
        JPanel page = UiUtil.page();
        UiUtil.styleTable(auditTable);
        page.add(UiUtil.title("Authorized Audit History"), BorderLayout.NORTH);
        page.add(new JScrollPane(auditTable), BorderLayout.CENTER);
        return page;
    }

    private void refreshAll() {
        contact.setText(student.getContactNumber());
        email.setText(student.getEmail());
        address.setText(student.getHomeAddress());
        List<ClearanceRecord> records = services.clearances.recordsForStudent(student);
        DefaultTableModel cm = new DefaultTableModel(new Object[]{"Entry ID", "Cycle", "Department", "Required", "Status", "Remarks", "Reviewed At"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        String summaryText = "No clearance records yet. Ask an administrator to generate one.";
        for (ClearanceRecord record : records) {
            summaryText = record.getTerm() + " " + record.getAcademicYear() + " | Progress " + record.getProgressPercent() + "% | " + record.getOverallStatus();
            for (DepartmentClearance entry : record.getEntries()) {
                cm.addRow(new Object[]{entry.getId(), record.getTerm() + " " + record.getAcademicYear(), entry.getDepartmentNameSnapshot(),
                        entry.isRequiredSnapshot() ? "Required" : "Optional", entry.getStatus().getLabel(), entry.getRemarks(), FormatUtil.dateTime(entry.getReviewedAt())});
            }
        }
        clearanceTable.setModel(cm);
        summary.setText(summaryText);

        DefaultTableModel am = new DefaultTableModel(new Object[]{"Time", "Action", "Actor", "Record", "Previous", "New", "Remarks"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (AuditEntry entry : services.audits.forStudent(student)) {
            am.addRow(new Object[]{FormatUtil.dateTime(entry.getTimestamp()), entry.getAction(), entry.getActingUsername(),
                    entry.getRecordReference(), entry.getPreviousValue(), entry.getNewValue(), entry.getRemarks()});
        }
        auditTable.setModel(am);
    }

    private String selectedId(JTable table, int column) {
        int row = table.getSelectedRow();
        if (row < 0) {
            throw new IllegalArgumentException("Select a row first.");
        }
        return table.getValueAt(table.convertRowIndexToModel(row), column).toString();
    }
}
