package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.NameChangeRequest;
import model.Student;
import model.User;
import service.AppServices;
import util.UiUtil;

public class UserSettingsPanel extends JPanel {
    private final AppServices services;
    private final User user;
    private final Runnable signOutAction;
    private final JTable nameTable = new JTable();
    private final JTextField proposedName = UiUtil.field(20);
    private final JTextArea reason = new JTextArea(4, 28);

    public UserSettingsPanel(AppServices services, User user, Runnable signOutAction) {
        this.services = services;
        this.user = user;
        this.signOutAction = signOutAction;
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        UiUtil.styleTabs(tabs);
        tabs.add("Password", new PasswordPanel(services, user));
        if (user instanceof Student) {
            tabs.add("Name Request", nameRequestPanel((Student) user));
        }
        tabs.add("Sign Out", signOutPanel());
        add(tabs, BorderLayout.CENTER);
        refreshNameRequests();
    }

    private JPanel nameRequestPanel(final Student student) {
        JPanel page = UiUtil.page();
        JPanel form = UiUtil.card();
        form.setLayout(new BorderLayout(8, 8));
        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 8));
        fields.setOpaque(false);
        fields.add(new JLabel("Proposed displayed name"));
        fields.add(proposedName);
        form.add(fields, BorderLayout.NORTH);
        reason.setLineWrap(true);
        reason.setWrapStyleWord(true);
        form.add(new JScrollPane(reason), BorderLayout.CENTER);
        JButton submit = UiUtil.accentButton("Submit name-change request");
        submit.addActionListener(e -> {
            try {
                services.names.submit(student, proposedName.getText(), reason.getText());
                proposedName.setText("");
                reason.setText("");
                refreshNameRequests();
                UiUtil.showInfo(this, "Name-change request submitted.");
            } catch (Exception ex) {
                UiUtil.showError(this, ex);
            }
        });
        form.add(submit, BorderLayout.SOUTH);
        UiUtil.styleTable(nameTable);
        page.add(UiUtil.title("Settings: Name Request"), BorderLayout.NORTH);
        page.add(form, BorderLayout.CENTER);
        page.add(new JScrollPane(nameTable), BorderLayout.SOUTH);
        return page;
    }

    private JPanel signOutPanel() {
        JPanel page = UiUtil.page();
        JPanel card = UiUtil.card();
        card.setLayout(new BorderLayout(8, 8));
        card.add(UiUtil.title("Sign Out"), BorderLayout.NORTH);
        card.add(new JLabel("End the current session and return to the login screen."), BorderLayout.CENTER);
        JButton signOut = UiUtil.accentButton("Sign out now");
        signOut.addActionListener(e -> signOutAction.run());
        card.add(signOut, BorderLayout.SOUTH);
        page.add(card, BorderLayout.NORTH);
        return page;
    }

    private void refreshNameRequests() {
        if (!(user instanceof Student)) {
            return;
        }
        Student student = (Student) user;
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Request ID", "Current", "Proposed", "Reason", "Status", "Decision Remarks"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (NameChangeRequest request : services.names.forStudent(student)) {
            model.addRow(new Object[]{request.getId(), request.getCurrentDisplayedName(), request.getProposedDisplayedName(),
                    request.getReason(), request.getStatus().getLabel(), request.getDecisionRemarks()});
        }
        nameTable.setModel(model);
    }
}
