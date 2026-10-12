package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.User;
import service.AppServices;
import util.UiUtil;

public class LoginFrame extends JFrame {
    private final AppServices services;
    private final JTextField username = UiUtil.field(20);
    private final JPasswordField password = UiUtil.password(20);

    public LoginFrame(AppServices services) {
        super("CCC-ACT Login");
        this.services = services;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);
        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiUtil.BG);

        JPanel hero = new JPanel(new BorderLayout(20, 20));
        hero.setBackground(UiUtil.NAVY);
        hero.setBorder(BorderFactory.createEmptyBorder(36, 42, 36, 42));
        JLabel title = new JLabel("<html><div style='color:white;font-size:28px;font-weight:bold'>CCC Advance Clearance Tracker</div>" +
                "<div style='color:#d8f3f5;font-size:13px'>In-memory Java Swing clearance management for student final project demonstrations.</div></html>");
        hero.add(title, BorderLayout.CENTER);
        ImageIcon logo = new ImageIcon("CCC_logo.png");
        if (logo.getIconWidth() > 0) {
            Image scaled = logo.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
            hero.add(new JLabel(new ImageIcon(scaled)), BorderLayout.WEST);
        }

        JPanel form = UiUtil.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        form.add(UiUtil.title("Sign in"), g);
        g.gridwidth = 1; g.gridy++;
        form.add(new JLabel("Username"), g);
        g.gridx = 1;
        form.add(username, g);
        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Password"), g);
        g.gridx = 1;
        form.add(UiUtil.passwordWithToggle(password), g);
        JButton login = UiUtil.accentButton("Sign in");
        JButton forgot = new JButton("Forgot password instructions");
        forgot.setBorderPainted(false);
        forgot.setContentAreaFilled(false);
        forgot.setForeground(UiUtil.NAVY);
        g.gridx = 0; g.gridy++; g.gridwidth = 2;
        form.add(login, g);
        g.gridy++;
        form.add(forgot, g);
        g.gridy++;
        form.add(new JLabel("<html><small>Demo password for all initial accounts: <b>Temp@123</b></small></html>"), g);

        login.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { login(); }
        });
        forgot.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                UiUtil.showInfo(LoginFrame.this, "For demo recovery, ask an administrator to select your account and issue a new temporary password. Unauthenticated password reset is intentionally not available.");
            }
        });

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UiUtil.BG);
        center.add(form);
        root.add(hero, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private void login() {
        try {
            User user = services.auth.login(username.getText(), new String(password.getPassword()));
            if (user.isTemporaryPassword()) {
                TemporaryPasswordDialog dialog = new TemporaryPasswordDialog(this, services, user);
                dialog.setVisible(true);
                if (user.isTemporaryPassword()) {
                    return;
                }
            }
            MainFrame main = new MainFrame(services, user, this);
            main.setVisible(true);
            setVisible(false);
            username.setText("");
            password.setText("");
        } catch (Exception ex) {
            UiUtil.showError(this, ex);
        }
    }
}
