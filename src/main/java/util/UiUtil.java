package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;

public final class UiUtil {
    public static final Color NAVY = new Color(0, 76, 102);
    public static final Color TEAL = new Color(32, 171, 173);
    public static final Color GOLD = new Color(255, 183, 0);
    public static final Color BG = new Color(239, 246, 249);
    public static final Color WHITE = Color.WHITE;
    public static final Color INK = new Color(4, 8, 12);
    public static final Color MUTED = new Color(138, 148, 164);

    private UiUtil() {
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(INK);
        return label;
    }

    public static JLabel smallTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(INK);
        return label;
    }

    public static JButton button(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBackground(NAVY);
        button.setForeground(INK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(new CompoundBorder(new MatteBorder(2, 2, 4, 4, INK), new EmptyBorder(8, 14, 8, 14)));
        return button;
    }

    public static JButton accentButton(String text) {
        JButton button = button(text);
        button.setBackground(GOLD);
        button.setForeground(INK);
        return button;
    }

    public static JButton lightButton(String text) {
        JButton button = button(text);
        button.setBackground(WHITE);
        button.setForeground(INK);
        return button;
    }

    public static JPanel page() {
        JPanel panel = new GridBackgroundPanel(new BorderLayout(16, 16));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(new CompoundBorder(new MatteBorder(3, 3, 5, 5, INK),
                new CompoundBorder(new MatteBorder(0, 0, 4, 4, GOLD), new EmptyBorder(14, 14, 14, 14))));
        return panel;
    }

    public static JPanel tealBanner(String eyebrow, String title, String subtitle) {
        JPanel banner = card();
        banner.setLayout(new BorderLayout(8, 4));
        banner.setBackground(NAVY);
        JLabel text = new JLabel("<html><div style='color:#ffc400;font-size:12px;font-weight:bold'>" + eyebrow +
                "</div><div style='color:white;font-size:28px;font-weight:bold'>" + title +
                "</div><div style='color:#d9f4f6;font-size:12px;font-weight:bold'>" + subtitle + "</div></html>");
        banner.add(text, BorderLayout.CENTER);
        return banner;
    }

    public static JTextField field(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(INK);
        field.setBackground(WHITE);
        field.setCaretColor(INK);
        field.setBorder(new CompoundBorder(new MatteBorder(2, 2, 3, 3, INK), new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    public static JPasswordField password(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(INK);
        field.setBackground(WHITE);
        field.setCaretColor(INK);
        field.setBorder(new CompoundBorder(new MatteBorder(2, 2, 3, 3, INK), new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    public static JPanel passwordWithToggle(final JPasswordField field) {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setOpaque(false);
        final char echo = field.getEchoChar();
        JButton toggle = lightButton("Show");
        toggle.setPreferredSize(new Dimension(76, 38));
        toggle.addActionListener(e -> {
            boolean hidden = field.getEchoChar() != (char) 0;
            field.setEchoChar(hidden ? (char) 0 : echo);
            toggle.setText(hidden ? "Hide" : "Show");
        });
        panel.add(field, BorderLayout.CENTER);
        panel.add(toggle, BorderLayout.EAST);
        return panel;
    }

    public static void styleTabs(JTabbedPane tabs) {
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(BG);
        tabs.setForeground(INK);
        tabs.setOpaque(true);
        tabs.setBorder(new EmptyBorder(8, 0, 0, 0));
        UIManager.put("TabbedPane.selected", GOLD);
        UIManager.put("TabbedPane.contentAreaColor", WHITE);
        UIManager.put("TabbedPane.focus", GOLD);
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTabComponents(tabs);
            }
        });
        SwingUtilities.invokeLater(() -> updateTabComponents(tabs));
    }

    private static void updateTabComponents(JTabbedPane tabs) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            JLabel label = new JLabel(tabs.getTitleAt(i));
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setBorder(new CompoundBorder(new MatteBorder(2, 2, 4, 4, INK), new EmptyBorder(8, 14, 8, 14)));
            if (i == tabs.getSelectedIndex()) {
                label.setBackground(GOLD);
                label.setForeground(INK);
            } else {
                label.setBackground(WHITE);
                label.setForeground(INK);
            }
            tabs.setTabComponentAt(i, label);
        }
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 251, 253));
                    c.setForeground(new Color(30, 43, 52));
                }
                return c;
            }
        });
    }

    public static void showError(Component parent, Exception ex) {
        JOptionPane.showMessageDialog(parent, ex.getMessage(), "CCC-ACT", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "CCC-ACT", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class GridBackgroundPanel extends JPanel {
        GridBackgroundPanel(LayoutManager layout) {
            super(layout);
            setBackground(BG);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(220, 232, 238));
            for (int x = 0; x < getWidth(); x += 24) {
                g2.drawLine(x, 0, x, getHeight());
            }
            for (int y = 0; y < getHeight(); y += 24) {
                g2.drawLine(0, y, getWidth(), y);
            }
            g2.dispose();
        }
    }
}
