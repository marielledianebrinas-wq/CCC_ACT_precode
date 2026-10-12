package ui;

import java.awt.*;
import javax.swing.*;
import model.*;
import service.AppServices;
import util.UiUtil;

public class MainFrame extends JFrame {
    private final AppServices services;
    private final User user;
    private final LoginFrame loginFrame;
    private final JPanel content = new JPanel(new BorderLayout());
    private final JPanel navBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
    private final JLabel breadcrumb = new JLabel();

    public MainFrame(AppServices services, User user, LoginFrame loginFrame) {
        super("CCC-ACT - " + user.getRole().getLabel());
        this.services = services;
        this.user = user;
        this.loginFrame = loginFrame;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1220, 760);
        setLocationRelativeTo(null);
        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = UiUtil.page();
        content.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);
        JPanel topBar = UiUtil.card();
        topBar.setLayout(new BorderLayout(12, 0));
        JLabel brand = new JLabel("<html><span style='font-size:20px;font-weight:bold;color:#004c66'>CCC-ACT</span> " +
                "<span style='font-size:13px;color:#8a94a4'>Advance Clearance Tracker</span></html>");
        topBar.add(brand, BorderLayout.WEST);
        navBar.setOpaque(false);

        if (user instanceof Student) {
            addNav("Dashboard", new StudentDashboardPanel(services, (Student) user));
        } else if (user instanceof DepartmentStaff) {
            addNav("Dashboard", new StaffDashboardPanel(services, (DepartmentStaff) user));
        } else if (user instanceof AdminUser) {
            addNav("Dashboard", new AdminDashboardPanel(services, (AdminUser) user));
        }
        Runnable signOut = () -> {
            dispose();
            loginFrame.setVisible(true);
        };
        addNav("Settings", new UserSettingsPanel(services, user, signOut));
        topBar.add(navBar, BorderLayout.EAST);

        String subtitle = user.getRole().getLabel() + " - " + user.getUsername();
        header.add(topBar, BorderLayout.NORTH);
        header.add(UiUtil.tealBanner(user.getRole().getLabel().toUpperCase(), "Hey, " + user.getDisplayName() + "!", subtitle), BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        breadcrumb.setOpaque(true);
        breadcrumb.setBackground(UiUtil.WHITE);
        breadcrumb.setForeground(UiUtil.INK);
        breadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 13));
        breadcrumb.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        center.add(breadcrumb, BorderLayout.NORTH);
        center.add(content, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        if (content.getComponentCount() == 0) {
            content.add(new JLabel("No dashboard available."), BorderLayout.CENTER);
        }
        return root;
    }

    private void addNav(String label, JPanel panel) {
        JButton button = "Dashboard".equals(label) ? UiUtil.accentButton(label) : UiUtil.button(label);
        button.addActionListener(e -> {
            updateBreadcrumb(label);
            content.removeAll();
            content.add(panel, BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        });
        navBar.add(button);
        if (content.getComponentCount() == 0) {
            updateBreadcrumb(label);
            content.add(panel, BorderLayout.CENTER);
        }
    }

    private void updateBreadcrumb(String current) {
        breadcrumb.setText("CCC-ACT  >  " + user.getRole().getLabel() + "  >  " + current);
    }
}
