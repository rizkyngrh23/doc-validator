package com.egov.docvalidation.desktopui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

public class VerifyPanelWithJwt extends JPanel {
    private final JTextField docIdField;
    private final JButton verifyButton;
    private final JLabel resultLabel;
    private final String jwt;
    private JButton listDocsButton;
    private JButton uploadButton;
    private JButton downloadButton;
    private JTable docTable;
    private DefaultTableModel docTableModel;
    private long userId = -1;

    private void setUserIdFromJwt() {
        String userIdStr = DocumentServiceClient.extractUserIdFromJwt(jwt);
        if (userIdStr != null) {
            try {
                userId = Long.parseLong(userIdStr);
            } catch (Exception e) {
                userId = -1;
            }
        }
    }

    public VerifyPanelWithJwt(String jwt) {
        this.jwt = jwt;
        setUserIdFromJwt();
        System.out.println("[DEBUG] Panel created, JWT: " + jwt);
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

        String[] columns = {"ID", "File Name", "Status", "Metadata"};
        docTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        docTable = new JTable(docTableModel);
        docTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(docTable);
        scrollPane.setPreferredSize(new Dimension(350, 80));
        scrollPane.setMinimumSize(new Dimension(350, 60));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(scrollPane, gbc);

        listDocsButton = new JButton("List My Documents");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        add(listDocsButton, gbc);
        uploadButton = new JButton("Upload Document");
        gbc.gridx = 1; gbc.gridy = 4;
        add(uploadButton, gbc);
        downloadButton = new JButton("Download Selected");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(downloadButton, gbc);

        verifyButton.addActionListener(e -> doVerify());
        listDocsButton.addActionListener(e -> doListDocs());
        uploadButton.addActionListener(e -> doUpload());
        downloadButton.addActionListener(e -> doDownload());
    }

    private void doVerify() {
        String docId = docIdField.getText().trim();
        if (docId.isEmpty()) {
            resultLabel.setText("Please enter a document ID.");
            return;
        }
        new Thread(() -> {
            try {
                String response = VerificationService.verifyDocument(docId, jwt);
                SwingUtilities.invokeLater(() -> resultLabel.setText(response));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> resultLabel.setText("Verification failed: " + ex.getMessage()));
            }
        }).start();
    }

    private void doListDocs() {
        System.out.println("[DEBUG] JWT: " + jwt);
        System.out.println("[DEBUG] userId: " + userId);
        if (userId == -1) {
            resultLabel.setText("Could not extract userId from JWT. Try logging in again.");
            return;
        }
        new Thread(() -> {
            try {
                List<DocumentServiceClient.DocumentDTO> docs = DocumentServiceClient.getDocumentsByUser(userId, jwt);
                SwingUtilities.invokeLater(() -> {
                    docTableModel.setRowCount(0);
                    for (var d : docs) {
                        String fileName = d.filePath;
                        int idx = fileName.lastIndexOf("/");
                        if (idx == -1) idx = fileName.lastIndexOf("\\");
                        if (idx != -1) fileName = fileName.substring(idx + 1);
                        docTableModel.addRow(new Object[]{d.docId, fileName, d.status, d.metadata});
                    }
                    resultLabel.setText("Fetched " + docs.size() + " documents.");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> resultLabel.setText("List failed: " + ex.getMessage()));
            }
        }).start();
    }

    private void doUpload() {
        if (userId == -1) {
            resultLabel.setText("List your documents first to set userId.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String metadata = JOptionPane.showInputDialog(this, "Enter metadata (optional):");
            new Thread(() -> {
                try {
                    String response = DocumentServiceClient.uploadDocument(userId, jwt, file, metadata);
                    SwingUtilities.invokeLater(() -> resultLabel.setText("Upload success: " + response));
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> resultLabel.setText("Upload failed: " + ex.getMessage()));
                }
            }).start();
        }
    }

    private void doDownload() {
        int selectedRow = docTable.getSelectedRow();
        if (selectedRow == -1) {
            resultLabel.setText("Select a document to download.");
            return;
        }
        String docIdStr = docTableModel.getValueAt(selectedRow, 0).toString();
        String fileName = docTableModel.getValueAt(selectedRow, 1).toString();
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(fileName));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            new Thread(() -> {
                try {
                    DocumentServiceClient.downloadDocument(java.util.UUID.fromString(docIdStr), jwt, dest);
                    SwingUtilities.invokeLater(() -> resultLabel.setText("Downloaded to: " + dest.getAbsolutePath()));
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> resultLabel.setText("Download failed: " + ex.getMessage()));
                }
            }).start();
        }
    }
}
