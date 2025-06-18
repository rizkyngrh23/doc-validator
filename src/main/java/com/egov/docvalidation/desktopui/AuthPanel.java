package com.egov.docvalidation.desktopui;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthPanel extends JPanel {
    private final JFrame parentFrame;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton registerButton;
    private final JLabel resultLabel;

    public AuthPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0; gbc.gridy = 0;
        add(emailLabel, gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 1;
        add(passwordLabel, gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        add(passwordField, gbc);

        loginButton = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 2;
        add(loginButton, gbc);
        registerButton = new JButton("Register");
        gbc.gridx = 1; gbc.gridy = 2;
        add(registerButton, gbc);

        resultLabel = new JLabel("");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(resultLabel, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRegister();
            }
        });
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            resultLabel.setText("Please enter email and password.");
            return;
        }
        new Thread(() -> {
            try {
                String response = AuthService.login(email, password);
                System.out.println("[DEBUG] Login response: " + response);
                String token = extractToken(response);
                System.out.println("[DEBUG] Extracted JWT: " + token);
                SwingUtilities.invokeLater(() -> {
                    if (token == null || token.isEmpty()) {
                        resultLabel.setText("Login failed: No token returned");
                    } else {
                        resultLabel.setText("Login successful!");
                        parentFrame.setContentPane(new VerifyPanelWithJwt(token));
                        parentFrame.revalidate();
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> resultLabel.setText("Login failed: " + ex.getMessage()));
            }
        }).start();
    }

    private String extractToken(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.has("token")) return obj.getString("token");
        } catch (Exception ignore) {}
        if (response != null && response.length() > 20 && !response.contains("{") && !response.contains("<html")) {
            return response.trim();
        }
        return "";
    }

    private void doRegister() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            resultLabel.setText("Please enter email and password.");
            return;
        }
        new Thread(() -> {
            try {
                String response = AuthService.register(email, password);
                SwingUtilities.invokeLater(() -> resultLabel.setText(response + " You can now login."));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> resultLabel.setText("Registration failed: " + ex.getMessage()));
            }
        }).start();
    }
}
