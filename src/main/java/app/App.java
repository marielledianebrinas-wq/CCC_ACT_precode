package app;

import data.SessionData;
import java.awt.EventQueue;
import javax.swing.UIManager;
import service.AppServices;
import ui.LoginFrame;

public class App {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }
                AppServices services = new AppServices(SessionData.createDemoData());
                new LoginFrame(services).setVisible(true);
            }
        });
    }
}
