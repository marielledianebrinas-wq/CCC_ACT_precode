package ui;

import java.awt.*;
import javax.swing.*;
import model.User;
import service.AppServices;
import util.UiUtil;

public class TemporaryPasswordDialog extends JDialog {
    public TemporaryPasswordDialog(Window owner, final AppServices services, final User user) {
        super(owner, "First-login password change", ModalityType.APPLICATION_MODAL);
        setSize(430, 220);
        setLocationRelativeTo(owner);
        JPanel panel = UiUtil.card();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        final JPasswordField pass = UiUtil.password(18);
        final JPasswordField confirm = UiUtil.password(18);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        panel.add(UiUtil.smallTitle("Create a new password before continuing"), g);
        g.gridwidth = 1; g.gridy++;
        panel.add(new JLabel("New password"), g);
        g.gridx = 1; panel.add(UiUtil.passwordWithToggle(pass), g);
        g.gridx = 0; g.gridy++;
        panel.add(new JLabel("Confirm"), g);
        g.gridx = 1; panel.add(UiUtil.passwordWithToggle(confirm), g);
        JButton save = UiUtil.accentButton("Save password");
        g.gridx = 0; g.gridy++; g.gridwidth = 2;
        panel.add(save, g);
        save.addActionListener(e -> {
            try {
                String p = new String(pass.getPassword());
                if (!p.equals(new String(confirm.getPassword()))) {
                    throw new IllegalArgumentException("Passwords do not match.");
                }
                services.auth.changeTemporaryPassword(user, p);
                dispose();
            } catch (Exception ex) {
                UiUtil.showError(this, ex);
            }
        });
        setContentPane(panel);
    }
}
