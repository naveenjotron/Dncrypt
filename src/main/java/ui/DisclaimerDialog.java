package ui;

import javax.swing.*;

import baseCodes.PreferencesManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DisclaimerDialog extends JDialog {
    private JCheckBox dontShowAgainCheckBox;
    private JButton agreeButton;
    private PreferencesManager preferencesManager;

    public DisclaimerDialog(Frame parent, PreferencesManager preferencesManager) {
        super(parent, "Disclaimer", true);
        this.preferencesManager = preferencesManager;
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(parent);

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText("<html><body style='margin:0;padding:0;'>"
                + "<h2 style='margin:0;'>Legal Disclaimer</h2>"
                
                + "<h3 style='margin:0;padding-top:5px;'>Beta Version Notice:</h3>"
                + "<p style='margin:0;padding:0;'>This application is currently in its beta version. As such, it may contain bugs, errors, and other issues. Users are advised that the application is still under development and may undergo significant changes.</p>"
                
                + "<h3 style='margin:0;padding-top:5px;'>User Responsibility:</h3>"
                + "<p style='margin:0;padding:0;'>Users are fully responsible for any misuse of this application. Any legal action resulting from improper or illegal use of the application will be solely the responsibility of the user. The developers and distributors of this application will not be held liable for any actions taken by users.</p>"
                
                + "<h3 style='margin:0;padding-top:5px;'>Use and Distribution:</h3>"
                + "<p style='margin:0;padding:0;'>This application is provided free of charge and is intended to be used \"as is.\" Users are not permitted to modify, redistribute, or sell the application in any form. Any unauthorized modification or distribution of this application is strictly prohibited.</p>"
                
                + "<p style='margin:0;padding-top:5px;'><strong>By using this application, you agree to the terms of this legal disclaimer.</strong></p>"
                + "</body></html>");
        textPane.setEditable(false);
        textPane.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        dontShowAgainCheckBox = new JCheckBox("Do not show this message again");
        bottomPanel.add(dontShowAgainCheckBox, BorderLayout.WEST);

        agreeButton = new JButton("I Agree");
        agreeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dontShowAgainCheckBox.isSelected()) {
                    preferencesManager.setDisclaimerShown(true);
                }
                dispose();
            }
        });
        bottomPanel.add(agreeButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Exit application if the dialog is closed without agreeing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    public static void showDisclaimer(Frame parent, PreferencesManager preferencesManager) {
        long currentTime = System.currentTimeMillis();
        long lastShownTime = preferencesManager.getTimestamp();
        long twoMonthsMillis = 60L * 24L * 60L * 60L * 1000L; // 2 months in milliseconds

        if (!preferencesManager.isDisclaimerShown() || (currentTime - lastShownTime) >= twoMonthsMillis) {
            DisclaimerDialog disclaimerDialog = new DisclaimerDialog(parent, preferencesManager);
            disclaimerDialog.setVisible(true);
        }
    }
}






