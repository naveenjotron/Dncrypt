package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import baseCodes.FileEncryptorDecryptor;
import baseCodes.PreferencesManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.InvalidKeyException;
import net.miginfocom.swing.MigLayout;

public class FileEncryptorDecryptorUI extends JFrame {

    private JTextField passwordField;
    private JTextField directoryField;
    private JCheckBox encryptFileNameCheckBox;
    JTextPane textPane;
    private PreferencesManager preferencesManager;
    private FileEncryptorDecryptor fileEncryptorDecryptor;

    public FileEncryptorDecryptorUI() {
        super("File Encryptor/Decryptor");
        
        String appDirectory = new File(".").getAbsolutePath(); // Use the current directory of the app
        preferencesManager = new PreferencesManager(appDirectory);
        fileEncryptorDecryptor = new FileEncryptorDecryptor();

        // Show the disclaimer dialog if needed
        DisclaimerDialog.showDisclaimer(this, preferencesManager);
        
        getContentPane().setLayout(new MigLayout("", "[][grow, fill][]", "[][grow, fill][]"));
        
        // Top Panel with FlowLayout for the "Select File/Folder" button
        JPanel topPanel = new JPanel(new FlowLayout());
        JButton fileChooserButton = new JButton("Select File/Folder");
        fileChooserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleFileSelection();
            }
        });
        topPanel.add(fileChooserButton);

        // Center Panel with MiGLayout for other components
        JPanel centerPanel = new JPanel(new MigLayout("wrap 2, insets 10", "[][grow,fill]", "[][][]10[30px:n][grow,fill]"));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Directory Field
        directoryField = new JTextField();
        centerPanel.add(new JLabel("Selected File/Folder:"), "cell 0 0");
        centerPanel.add(directoryField, "cell 1 0, growx, height 25:push");

        // Password components
        passwordField = new JPasswordField();
        centerPanel.add(new JLabel("Enter Password:"), "cell 0 1");
        centerPanel.add(passwordField, "cell 1 1, growx, height 25:push");
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        JButton encryptButton = new JButton("Encrypt Files");
        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleFileProcessing((file, password, encryptFileName) -> {
                    fileEncryptorDecryptor.encryptFile(file, password, encryptFileName);
                    return 1;
                });
            }
        });
        JButton decryptButton = new JButton("Decrypt Files");
        decryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleFileProcessing((file, password, encryptFileName) -> {
                    fileEncryptorDecryptor.decryptFile(file, password, encryptFileName);
                    return 1;
                });
            }
        });
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        centerPanel.add(buttonPanel, "cell 0 3 2 1, growx, alignx center"); // Center the buttons horizontally

        // Encrypt CheckBox
        encryptFileNameCheckBox = new JCheckBox("Encrypt File Names");
        centerPanel.add(encryptFileNameCheckBox, "cell 0 2 2 1");
        
        // Text Pane with JScrollPane
        JScrollPane scrollPane = new JScrollPane();
        textPane = new JTextPane();
        scrollPane.setViewportView(textPane);
        centerPanel.add(scrollPane, "cell 0 4 2 1, grow");
        
        // Add components to the main panel
        getContentPane().add(topPanel, "cell 1 0, growx");
        getContentPane().add(centerPanel, "cell 1 1, grow");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 369);  // Set an initial width of 600
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
        }
    }

    private void handleFileProcessing(FileProcessor fileProcessor) {
        if (directoryField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a file or folder first.");
            return;
        }

        File selectedFile = new File(directoryField.getText());

        try {
            int fileCount = processFiles(selectedFile, passwordField.getText(), encryptFileNameCheckBox.isSelected(), fileProcessor);
            textPane.setText("Files processed successfully. Count: " + fileCount);
        } catch (InvalidKeyException e) {
            JOptionPane.showMessageDialog(this, "Incorrect password. Please try again.");
            textPane.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            textPane.setText("");
        }
    }

    private int processFiles(File file, String password, boolean encryptFileName, FileProcessor fileProcessor) throws Exception {
        int fileCount = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File childFile : files) {
                    fileProcessor.processFile(childFile, password, encryptFileName);
                    fileCount++;
                }
            }
        } else {
            fileProcessor.processFile(file, password, encryptFileName);
            fileCount++;
        }
        return fileCount;
    }

    @FunctionalInterface
    private interface FileProcessor {
        int processFile(File file, String password, boolean encryptFileName) throws Exception;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileEncryptorDecryptorUI::new);
    }
}
