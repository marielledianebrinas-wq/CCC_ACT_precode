package ui;

import java.awt.*;
import javax.swing.*;
import model.User;
import service.AppServices;
import util.UiUtil;

public class PasswordPanel extends JPanel {
    public PasswordPanel(final AppServices services, final User user) {
        setLayout(new BorderLayout());
        setBackground(UiUtil.BG);
        JPanel page = UiUtil.page();
        JPanel form = UiUtil.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        final JPasswordField current = UiUtil.password(18);
        final JPasswordField next = UiUtil.password(18);
        final JPasswordField confirm = UiUtil.password(18);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        form.add(UiUtil.title("Change Password"), g);
        g.gridwidth = 1; g.gridy++;
        form.add(new JLabel("Current password"), g); g.gridx = 1; form.add(UiUtil.passwordWithToggle(current), g);
        g.gridx = 0; g.gridy++;
        form.add(new JLabel("New password"), g); g.gridx = 1; form.add(UiUtil.passwordWithToggle(next), g);
        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Confirm"), g); g.gridx = 1; form.add(UiUtil.passwordWithToggle(confirm), g);
        JButton save = UiUtil.accentButton("Update password");
        g.gridx = 0; g.gridy++; g.gridwidth = 2;
        form.add(save, g);
        save.addActionListener(e -> {
            try {
                String n = new String(next.getPassword());
                if (!n.equals(new String(confirm.getPassword()))) {
                    throw new IllegalArgumentException("Passwords do not match.");
                }
                services.auth.changePassword(user, new String(current.getPassword()), n);
                current.setText(""); next.setText(""); confirm.setText("");
                UiUtil.showInfo(this, "Password updated.");
            } catch (Exception ex) {
                UiUtil.showError(this, ex);
            }
        });
        page.add(form, BorderLayout.NORTH);
        add(page, BorderLayout.CENTER);
    }
}
