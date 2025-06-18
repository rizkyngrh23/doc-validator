package com.egov.docvalidation.desktopui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerifyPanel extends JPanel {
    private final JTextField docIdField;
    private final JButton verifyButton;
    private final JLabel resultLabel;

    public VerifyPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel docIdLabel = new JLabel("Document ID (UUID):");
        gbc.gridx = 0; gbc.gridy = 0;
        add(docIdLabel, gbc);
        docIdField = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 0;
        add(docIdField, gbc);

        verifyButton = new JButton("Verify");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(verifyButton, gbc);

        resultLabel = new JLabel("");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(resultLabel, gbc);

        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doVerify();
            }
        });
    }

    private void doVerify() {
        String docId = docIdField.getText().trim();
        if (docId.isEmpty()) {
            resultLabel.setText("Please enter a document ID.");
            return;
        }
        // TODO: Call VerificationService.verifyDocument and handle result
        resultLabel.setText("(Verification logic not implemented)");
    }
}
