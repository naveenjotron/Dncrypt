package ui;

import javax.swing.*;

import baseCodes.FileEncryptorDecryptor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.InvalidKeyException;

public class FileEncryptorDecryptorUI extends JFrame {

    private JTextField passwordField;
    private JTextField directoryField;
    private JLabel statusLabel;

    public FileEncryptorDecryptorUI() {
        super("File Encryptor/Decryptor");

        JLabel passwordLabel = new JLabel("Enter Password:");
        passwordField = new JPasswordField();
        JButton encryptButton = new JButton("Encrypt Files");
        JButton decryptButton = new JButton("Decrypt Files");
        JButton fileChooserButton = new JButton("Select File/Folder");
        statusLabel = new JLabel();

        fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleFileSelection();
            }
        });

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleFileProcessing(FileEncryptorDecryptor::encryptFiles);
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleFileProcessing(FileEncryptorDecryptor::decryptFiles);
            }
        });

        directoryField = new JTextField();
        directoryField.setEditable(false);

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(fileChooserButton);
        panel.add(directoryField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        panel.add(buttonPanel);
        panel.add(statusLabel);

        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleFileSelection() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File or Folder");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            directoryField.setText(selectedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Selected File/Folder: " + selectedFile.getAbsolutePath());
        }
    }

    private void handleFileProcessing(FileProcessor fileProcessor) {
        if (directoryField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a file or folder first.");
            return;
        }

        File selectedFile = new File(directoryField.getText());

        try {
            int fileCount = fileProcessor.processFiles(selectedFile, passwordField.getText());
            statusLabel.setText("Files processed successfully. Count: " + fileCount);
        } catch (InvalidKeyException e) {
            JOptionPane.showMessageDialog(this, "Incorrect password. Please try again.");
            statusLabel.setText("");
        }
    }

    @FunctionalInterface
    private interface FileProcessor {
        int processFiles(File file, String password) throws InvalidKeyException;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileEncryptorDecryptorUI());
    }
}









