package ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import service.AppServices;
import util.FormatUtil;
import util.UiUtil;

public class AdminDashboardPanel extends JPanel {
    private final AppServices services;
    private final AdminUser admin;
    private final JTable accounts = new JTable();
    private final JTable departments = new JTable();
    private final JTable students = new JTable();
    private final JTable generateStudents = new JTable();
    private final JTable clearances = new JTable();
    private final JTable names = new JTable();
    private final JTable audit = new JTable();
    private final JLabel summary = UiUtil.smallTitle("");
    private final JComboBox<String> term = new JComboBox<String>(new String[]{"First Semester", "Second Semester"});
    private final JTextField academicYear = UiUtil.field(10);

    public AdminDashboardPanel(AppServices services, AdminUser admin) {
        this.services = services;
        this.admin = admin;
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        UiUtil.styleTabs(tabs);
        tabs.add("Summary", summaryPanel());
        tabs.add("Accounts", accountsPanel());
        tabs.add("Students", studentsPanel());
        tabs.add("Departments & Semester", departmentsPanel());
        tabs.add("Generate Clearance", generatePanel());
        tabs.add("Clearance Records", clearancesPanel());
        tabs.add("Name Requests", namesPanel());
        tabs.add("Audit", auditPanel());
        add(tabs, BorderLayout.CENTER);
        refreshAll();
    }

    private JPanel summaryPanel() {
        JPanel page = UiUtil.page();
        JPanel card = UiUtil.card();
        card.setLayout(new BorderLayout(8, 8));
        card.add(UiUtil.title("Administrator Dashboard"), BorderLayout.NORTH);
        card.add(summary, BorderLayout.CENTER);
        page.add(card, BorderLayout.NORTH);
        return page;
    }

    private JPanel accountsPanel() {
        JPanel page = UiUtil.page();
        JPanel buttons = UiUtil.card();
        buttons.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton create = UiUtil.accentButton("Create Staff");
        JButton active = UiUtil.button("Activate/Deactivate");
        JButton reset = UiUtil.button("Issue Temporary Password");
        create.addActionListener(e -> createStaff());
        active.addActionListener(e -> toggleAccount());
        reset.addActionListener(e -> resetPassword());
        buttons.add(UiUtil.smallTitle("Account Management"));
        buttons.add(create); buttons.add(active); buttons.add(reset);
        UiUtil.styleTable(accounts);
        page.add(buttons, BorderLayout.NORTH);
        page.add(new JScrollPane(accounts), BorderLayout.CENTER);
        return page;
    }

    private JPanel studentsPanel() {
        JPanel page = UiUtil.page();
        JPanel buttons = UiUtil.card();
        JButton register = UiUtil.accentButton("Register Student");
        JButton edit = UiUtil.accentButton("Edit Selected Authoritative Profile");
        register.addActionListener(e -> registerStudent());
        edit.addActionListener(e -> editStudent());
        buttons.add(UiUtil.smallTitle("Student Records"));
        buttons.add(register);
        buttons.add(edit);
        UiUtil.styleTable(students);
        page.add(buttons, BorderLayout.NORTH);
        page.add(new JScrollPane(students), BorderLayout.CENTER);
        return page;
    }

    private JPanel departmentsPanel() {
        JPanel page = UiUtil.page();
        JPanel controls = UiUtil.card();
        controls.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton saveDept = UiUtil.button("Update Selected Department");
        JButton saveSemester = UiUtil.accentButton("Save Active Semester");
        saveDept.addActionListener(e -> updateDepartment());
        saveSemester.addActionListener(e -> {
            try {
                services.departments.setActiveSemester(admin, term.getSelectedItem().toString(), academicYear.getText());
                refreshAll();
                UiUtil.showInfo(this, "Active semester updated.");
            } catch (Exception ex) {
                UiUtil.showError(this, ex);
            }
        });
        controls.add(UiUtil.smallTitle("Configuration"));
        controls.add(new JLabel("Term"));
        controls.add(term);
        controls.add(new JLabel("Academic year"));
        controls.add(academicYear);
        controls.add(saveSemester);
        controls.add(saveDept);
        UiUtil.styleTable(departments);
        page.add(controls, BorderLayout.NORTH);
        page.add(new JScrollPane(departments), BorderLayout.CENTER);
        return page;
    }

    private JPanel generatePanel() {
        JPanel page = UiUtil.page();
        JPanel top = UiUtil.card();
        top.setLayout(new BorderLayout(8, 8));
        top.add(UiUtil.title("Bulk Clearance Generation"), BorderLayout.NORTH);
        top.add(new JLabel("Select one or more students below. Duplicate records for the same cycle are rejected."), BorderLayout.CENTER);
        JButton generate = UiUtil.accentButton("Generate for Selected Students");
        generate.addActionListener(e -> generateClearances());
        top.add(generate, BorderLayout.SOUTH);
        JTable table = generateStudents;
        UiUtil.styleTable(table);
        page.add(top, BorderLayout.NORTH);
        page.add(new JScrollPane(table), BorderLayout.CENTER);
        return page;
    }

    private JPanel clearancesPanel() {
        JPanel page = UiUtil.page();
        UiUtil.styleTable(clearances);
        page.add(UiUtil.title("All Clearance Records"), BorderLayout.NORTH);
        page.add(new JScrollPane(clearances), BorderLayout.CENTER);
        return page;
    }

    private JPanel namesPanel() {
        JPanel page = UiUtil.page();
        JPanel controls = UiUtil.card();
        JButton approve = UiUtil.accentButton("Approve Selected");
        JButton reject = UiUtil.button("Reject Selected");
        approve.addActionListener(e -> decideName(true));
        reject.addActionListener(e -> decideName(false));
        controls.add(UiUtil.smallTitle("Displayed-Name Requests"));
        controls.add(approve);
        controls.add(reject);
        UiUtil.styleTable(names);
        page.add(controls, BorderLayout.NORTH);
        page.add(new JScrollPane(names), BorderLayout.CENTER);
        return page;
    }

    private JPanel auditPanel() {
        JPanel page = UiUtil.page();
        UiUtil.styleTable(audit);
        page.add(UiUtil.title("Full Audit History"), BorderLayout.NORTH);
        page.add(new JScrollPane(audit), BorderLayout.CENTER);
        return page;
    }

    private void createStaff() {
        try {
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            JTextField username = UiUtil.field(18);
            JTextField name = UiUtil.field(18);
            JPasswordField tempPass = UiUtil.password(18);
            JList<String> deptList = new JList<String>(departmentOptions());
            deptList.setVisibleRowCount(8);
            form.add(new JLabel("Username")); form.add(username);
            form.add(new JLabel("Staff name")); form.add(name);
            form.add(new JLabel("Temporary password")); form.add(tempPass);
            form.add(new JLabel("Departments")); form.add(new JScrollPane(deptList));
            int result = JOptionPane.showConfirmDialog(this, form, "Create staff account", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }
            List<String> ids = new ArrayList<String>();
            for (String selected : deptList.getSelectedValuesList()) {
                ids.add(selected.substring(0, selected.indexOf(" - ")));
            }
            services.users.createStaff(admin, username.getText(), new String(tempPass.getPassword()), name.getText(), ids);
            refreshAll();
        } catch (Exception ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void registerStudent() {
        try {
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            JTextField username = UiUtil.field(18);
            JPasswordField tempPass = UiUtil.password(18);
            JTextField studentId = UiUtil.field(18);
            JTextField name = UiUtil.field(18);
            JTextField program = UiUtil.field(18);
            JTextField year = UiUtil.field(18);
            JTextField contact = UiUtil.field(18);
            JTextField email = UiUtil.field(18);
            JTextField address = UiUtil.field(18);
            form.add(new JLabel("Username")); form.add(username);
            form.add(new JLabel("Temporary password")); form.add(UiUtil.passwordWithToggle(tempPass));
            form.add(new JLabel("Student ID")); form.add(studentId);
            form.add(new JLabel("Displayed name")); form.add(name);
            form.add(new JLabel("Program")); form.add(program);
            form.add(new JLabel("Year level")); form.add(year);
            form.add(new JLabel("Contact number")); form.add(contact);
            form.add(new JLabel("Email")); form.add(email);
            form.add(new JLabel("Home address")); form.add(address);
            int result = JOptionPane.showConfirmDialog(this, form, "Register student account", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }
            services.users.createStudent(admin, username.getText(), new String(tempPass.getPassword()), studentId.getText(),
                    name.getText(), program.getText(), year.getText(), contact.getText(), email.getText(), address.getText());
            refreshAll();
            UiUtil.showInfo(this, "Student account registered. The student must change the temporary password on first login.");
        } catch (Exception ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void toggleAccount() {
        try {
            User user = services.users.getById(selectedId(accounts, 0));
            boolean active = JOptionPane.showConfirmDialog(this, "Activate this account?", "Account status", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            services.users.setActive(admin, user, active);
            refreshAll();
        } catch (Exception ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void resetPassword() {
        try {
            User user = services.users.getById(selectedId(accounts, 0));
            String pass = JOptionPane.showInputDialog(this, "New temporary password", "Temp@123");
            if (pass == null) {
                return;
            }
            services.users.resetPassword(admin, user, pass);
            refreshAll();
        } catch (Exception ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void editStudent() {
        try {
            Student s = (Student) services.users.getById(selectedId(students, 0));
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            JTextField studentId = UiUtil.field(18); studentId.setText(s.getStudentId());
            JTextField name = UiUtil.field(18); name.setText(s.getDisplayName());
            JTextField program = UiUtil.field(18); program.setText(s.getProgram());
            JTextField year = UiUtil.field(18); year.setText(s.getYearLevel());
            form.add(new JLabel("Student ID")); form.add(studentId);
            form.add(new JLabel("Displayed name")); form.add(name);
            form.add(new JLabel("Program")); form.add(program);
            form.add(new JLabel("Year level")); form.add(year);
            int result = JOptionPane.showConfirmDialog(this, form, "Edit authoritative student record", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                services.users.updateStudentAuthority(admin, s, studentId.getText(), name.getText(), program.getText(), year.getText());
                refreshAll();
            }
        } catch (Exception ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void updateDepartment() {
        try {
            Department d = services.departments.get(selectedId(departments, 0));
            JCheckBox required = new JCheckBox("Required", d.isRequired());
            JCheckBox active = new JCheckBox("Active", d.isActive());
            JPanel p = new JPanel(new GridLayout(0, 1));
            p.add(new JLabel(d.getName()));
            p.add(required);
            p.add(active);
            int result = JOptionPane.showConfirmDialog(this, p, "Update department", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                services.departments.updateDepartment(admin, d, required.isSelected(), active.isSelected());
                refreshAll();
            }
        } catch (Exception ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void generateClearances() {
        try {
            int[] rows = generateStudents.getSelectedRows();
            if (rows.length == 0) {
                throw new IllegalArgumentException("Select at least one student.");
            }
            List<Student> selected = new ArrayList<Student>();
            for (int row : rows) {
                selected.add((Student) services.users.getById(generateStudents.getValueAt(generateStudents.convertRowIndexToModel(row), 0).toString()));
            }
            List<ClearanceRecord> created = services.clearances.generateForStudents(admin, selected);
            refreshAll();
            UiUtil.showInfo(this, "Generated " + created.size() + " clearance record(s).");
        } catch (Exception ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void decideName(boolean approve) {
        try {
            String id = selectedId(names, 0);
            NameChangeRequest target = null;
            for (NameChangeRequest request : services.names.all(admin)) {
                if (request.getId().equals(id)) {
                    target = request;
                    break;
                }
            }
            JTextArea remarks = new JTextArea(5, 32);
            int result = JOptionPane.showConfirmDialog(this, new JScrollPane(remarks),
                    approve ? "Approval remarks" : "Rejection remarks", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                services.names.decide(admin, target, approve, remarks.getText());
                refreshAll();
            }
        } catch (Exception ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void refreshAll() {
        term.setSelectedItem(services.data.getActiveSemester().getTerm());
        academicYear.setText(services.data.getActiveSemester().getAcademicYear());
        int studentCount = services.users.students().size();
        int staffCount = services.users.staffUsers().size();
        int clearanceCount = services.data.getClearanceRecords().size();
        int pendingNames = 0;
        for (NameChangeRequest r : services.names.all(admin)) {
            if (r.getStatus() == RequestStatus.PENDING) {
                pendingNames++;
            }
        }
        summary.setText("<html>Active cycle: <b>" + services.data.getActiveSemester().label() + "</b><br>Students: " + studentCount +
                " | Staff: " + staffCount + " | Clearance records: " + clearanceCount + " | Pending name requests: " + pendingNames +
                "<br><br>Data is in-memory only and resets when the app closes.</html>");
        refreshAccounts();
        refreshStudents();
        refreshDepartments();
        refreshClearances();
        refreshNames();
        refreshAudit();
    }

    private void refreshAccounts() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{"User ID", "Username", "Name", "Role", "Active", "Temporary Password"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (User u : services.users.allUsers()) {
            m.addRow(new Object[]{u.getId(), u.getUsername(), u.getDisplayName(), u.getRole().getLabel(), u.isActive(), u.isTemporaryPassword()});
        }
        accounts.setModel(m);
    }

    private void refreshStudents() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{"User ID", "Student ID", "Name", "Program", "Year", "Email", "Contact"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Student s : services.users.students()) {
            m.addRow(new Object[]{s.getId(), s.getStudentId(), s.getDisplayName(), s.getProgram(), s.getYearLevel(), s.getEmail(), s.getContactNumber()});
        }
        students.setModel(m);
        DefaultTableModel gm = new DefaultTableModel(new Object[]{"User ID", "Student ID", "Name", "Program", "Year", "Email", "Contact"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (int i = 0; i < m.getRowCount(); i++) {
            gm.addRow(new Object[]{m.getValueAt(i, 0), m.getValueAt(i, 1), m.getValueAt(i, 2),
                    m.getValueAt(i, 3), m.getValueAt(i, 4), m.getValueAt(i, 5), m.getValueAt(i, 6)});
        }
        generateStudents.setModel(gm);
    }

    private void refreshDepartments() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{"Department ID", "Name", "Required", "Active"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Department d : services.departments.departments()) {
            m.addRow(new Object[]{d.getId(), d.getName(), d.isRequired(), d.isActive()});
        }
        departments.setModel(m);
    }

    private void refreshClearances() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{"Record ID", "Student", "Cycle", "Progress", "Overall", "Department", "Required", "Department Status", "Remarks"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (ClearanceRecord r : services.clearances.allRecords(admin)) {
            Student s = (Student) services.users.getById(r.getStudentUserId());
            for (DepartmentClearance e : r.getEntries()) {
                m.addRow(new Object[]{r.getId(), s.getDisplayName(), r.getTerm() + " " + r.getAcademicYear(), r.getProgressPercent() + "%",
                        r.getOverallStatus(), e.getDepartmentNameSnapshot(), e.isRequiredSnapshot(), e.getStatus().getLabel(), e.getRemarks()});
            }
        }
        clearances.setModel(m);
    }

    private void refreshNames() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{"Request ID", "Student", "Current", "Proposed", "Reason", "Status", "Decision At", "Remarks"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (NameChangeRequest r : services.names.all(admin)) {
            Student s = (Student) services.users.getById(r.getStudentUserId());
            m.addRow(new Object[]{r.getId(), s.getDisplayName(), r.getCurrentDisplayedName(), r.getProposedDisplayedName(),
                    r.getReason(), r.getStatus().getLabel(), FormatUtil.dateTime(r.getDecisionAt()), r.getDecisionRemarks()});
        }
        names.setModel(m);
    }

    private void refreshAudit() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{"Time", "Action", "Actor", "Record", "Previous", "New", "Remarks"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (AuditEntry e : services.audits.all(admin)) {
            m.addRow(new Object[]{FormatUtil.dateTime(e.getTimestamp()), e.getAction(), e.getActingUsername(),
                    e.getRecordReference(), e.getPreviousValue(), e.getNewValue(), e.getRemarks()});
        }
        audit.setModel(m);
    }

    private String[] departmentOptions() {
        List<String> opts = new ArrayList<String>();
        for (Department d : services.departments.departments()) {
            opts.add(d.getId() + " - " + d.getName());
        }
        return opts.toArray(new String[0]);
    }

    private String selectedId(JTable table, int column) {
        int row = table.getSelectedRow();
        if (row < 0) {
            throw new IllegalArgumentException("Select a row first.");
        }
        return table.getValueAt(table.convertRowIndexToModel(row), column).toString();
    }
}
