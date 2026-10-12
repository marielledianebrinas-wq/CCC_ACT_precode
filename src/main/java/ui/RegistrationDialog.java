package ui;

import java.awt.*;
import javax.swing.*;
import service.AppServices;
import util.UiUtil;

public class RegistrationDialog extends JDialog {
    public RegistrationDialog(Window owner, final AppServices services) {
        super(owner, "Student Registration", ModalityType.APPLICATION_MODAL);
        setSize(560, 500);
        setLocationRelativeTo(owner);
        JPanel panel = UiUtil.page();
        JPanel form = UiUtil.card();
        form.setLayout(new GridLayout(0, 2, 8, 8));
        final JTextField username = UiUtil.field(20);
        final JPasswordField password = UiUtil.password(20);
        final JTextField studentId = UiUtil.field(20);
        final JTextField name = UiUtil.field(20);
        final JTextField program = UiUtil.field(20);
        final JTextField year = UiUtil.field(20);
        final JTextField contact = UiUtil.field(20);
        final JTextField email = UiUtil.field(20);
        final JTextField address = UiUtil.field(20);
        form.add(new JLabel("Username")); form.add(username);
        form.add(new JLabel("Temporary password")); form.add(UiUtil.passwordWithToggle(password));
        form.add(new JLabel("Student ID")); form.add(studentId);
        form.add(new JLabel("Displayed name")); form.add(name);
        form.add(new JLabel("Program")); form.add(program);
        form.add(new JLabel("Year level")); form.add(year);
        form.add(new JLabel("Contact number")); form.add(contact);
        form.add(new JLabel("Email")); form.add(email);
        form.add(new JLabel("Home address")); form.add(address);
        JButton save = UiUtil.accentButton("Create student account");
        save.addActionListener(e -> {
            try {
                services.users.registerStudent(username.getText(), new String(password.getPassword()), studentId.getText(),
                        name.getText(), program.getText(), year.getText(), contact.getText(), email.getText(), address.getText());
                UiUtil.showInfo(this, "Registration successful. Sign in and change the temporary password.");
                dispose();
            } catch (Exception ex) {
                UiUtil.showError(this, ex);
            }
        });
        panel.add(UiUtil.title("Student Registration"), BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(save, BorderLayout.SOUTH);
        setContentPane(panel);
    }
}
