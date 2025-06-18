package com.egov.docvalidation.desktopui;

import javax.swing.*;
import java.awt.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Document Validation System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());
            AuthPanel authPanel = new AuthPanel(frame);
            frame.add(authPanel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
