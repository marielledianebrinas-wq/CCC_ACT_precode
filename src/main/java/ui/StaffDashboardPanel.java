package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import service.AppServices;
import util.FormatUtil;
import util.UiUtil;

public class StaffDashboardPanel extends JPanel {
    private final AppServices services;
    private final DepartmentStaff staff;
    private final JTable queueTable = new JTable();
    private final JTable auditTable = new JTable();
    private final JComboBox<String> departmentFilter = new JComboBox<String>();
    private final JComboBox<String> statusFilter = new JComboBox<String>(new String[]{"", "PENDING", "FLAGGED", "APPROVED"});
    private final JTextField search = UiUtil.field(20);

    public StaffDashboardPanel(AppServices services, DepartmentStaff staff) {
        this.services = services;
        this.staff = staff;
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        UiUtil.styleTabs(tabs);
        tabs.add("Review Queue", queuePanel());
        tabs.add("Audit", auditPanel());
        add(tabs, BorderLayout.CENTER);
        refresh();
    }

    private JPanel queuePanel() {
        JPanel page = UiUtil.page();
        JPanel controls = UiUtil.card();
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        departmentFilter.addItem("");
        for (String deptId : staff.getAssignedDepartmentIds()) {
            Department d = services.departments.get(deptId);
            if (d != null) {
                departmentFilter.addItem(deptId + " - " + d.getName());
            }
        }
        JButton apply = UiUtil.button("Apply Filters");
        JButton approve = UiUtil.accentButton("Approve Selected");
        JButton flag = UiUtil.button("Flag Selected");
        apply.addActionListener(e -> refresh());
        approve.addActionListener(e -> review(true));
        flag.addActionListener(e -> review(false));
        controls.add(UiUtil.smallTitle("Assigned Clearance Queue"));
        controls.add(new JLabel("Department"));
        controls.add(departmentFilter);
        controls.add(new JLabel("Status"));
        controls.add(statusFilter);
        controls.add(new JLabel("Search"));
        controls.add(search);
        controls.add(apply);
        controls.add(approve);
        controls.add(flag);
        UiUtil.styleTable(queueTable);
        page.add(controls, BorderLayout.NORTH);
        page.add(new JScrollPane(queueTable), BorderLayout.CENTER);
        return page;
    }

    private JPanel auditPanel() {
        JPanel page = UiUtil.page();
        UiUtil.styleTable(auditTable);
        page.add(UiUtil.title("Authorized Department Audit History"), BorderLayout.NORTH);
        page.add(new JScrollPane(auditTable), BorderLayout.CENTER);
        return page;
    }

    private void review(boolean approve) {
        try {
            String entryId = selectedId(queueTable, 0);
            JTextArea remarks = new JTextArea(5, 32);
            int result = JOptionPane.showConfirmDialog(this, new JScrollPane(remarks),
                    approve ? "Approval remarks" : "Flag reason", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }
            if (approve) {
                services.clearances.approve(staff, entryId, remarks.getText());
            } else {
                services.clearances.flag(staff, entryId, remarks.getText());
            }
            refresh();
            UiUtil.showInfo(this, approve ? "Clearance approved." : "Clearance flagged.");
        } catch (Exception ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void refresh() {
        String dept = "";
        Object deptSelected = departmentFilter.getSelectedItem();
        if (deptSelected != null && deptSelected.toString().contains(" - ")) {
            dept = deptSelected.toString().substring(0, deptSelected.toString().indexOf(" - "));
        }
        DefaultTableModel qm = new DefaultTableModel(new Object[]{"Entry ID", "Student", "Student ID", "Department", "Required", "Status", "Remarks", "Reviewed At"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (DepartmentClearance entry : services.clearances.queueForStaff(staff, dept,
                statusFilter.getSelectedItem() == null ? "" : statusFilter.getSelectedItem().toString(), search.getText())) {
            Student student = services.clearances.studentFor(entry);
            qm.addRow(new Object[]{entry.getId(), student.getDisplayName(), student.getStudentId(), entry.getDepartmentNameSnapshot(),
                    entry.isRequiredSnapshot() ? "Required" : "Optional", entry.getStatus().getLabel(), entry.getRemarks(), FormatUtil.dateTime(entry.getReviewedAt())});
        }
        queueTable.setModel(qm);

        DefaultTableModel am = new DefaultTableModel(new Object[]{"Time", "Action", "Actor", "Record", "Previous", "New", "Remarks"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (AuditEntry entry : services.audits.forStaff(staff)) {
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
