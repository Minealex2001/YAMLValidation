package com.validador.ui.mainWindow.panels;

import com.validador.ui.language.TextGestor;

import javax.swing.*;
import java.awt.*;

public class ResultsPanel extends JPanel {
    private final JTabbedPane tabbedPane;
    private final WrapTextPane appValidationOutput;
    private final WrapTextPane spectralValidationOutput;

    public ResultsPanel(TextGestor textos) {
        setLayout(new BorderLayout());
        setOpaque(false);

        tabbedPane = new JTabbedPane();
        appValidationOutput = new WrapTextPane();
        spectralValidationOutput = new WrapTextPane();

        appValidationOutput.setEditable(false);
        spectralValidationOutput.setEditable(false);

        tabbedPane.addTab(textos.get("tab.appValidations"), new JScrollPane(appValidationOutput));
        tabbedPane.addTab(textos.get("tab.spectralValidations"), new JScrollPane(spectralValidationOutput));

        add(tabbedPane, BorderLayout.CENTER);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void actualizarTextos(TextGestor textos) {
        tabbedPane.setTitleAt(0, textos.get("tab.appValidations"));
        tabbedPane.setTitleAt(1, textos.get("tab.spectralValidations"));
    }

    public WrapTextPane getAppValidationOutput() {
        return appValidationOutput;
    }

    public WrapTextPane getSpectralValidationOutput() {
        return spectralValidationOutput;
    }

    public static void appendSpectralStyledBlock(JTextPane textPane, String spectralOutput) {
        String[] lines = spectralOutput.split("\r?\n");
        Color currentColor = new Color(238, 238, 238);
        boolean bold = false;
        for (String line : lines) {
            String upper = line.toUpperCase();
            if (upper.contains("ERROR")) {
                currentColor = new Color(255, 85, 85);
                bold = true;
            } else if (upper.contains("WARNING")) {
                currentColor = new Color(255, 180, 60);
                bold = true;
            } else if (upper.contains("INFO")) {
                currentColor = new Color(0, 173, 181);
                bold = false;
            }
            javax.swing.text.SimpleAttributeSet attrs = new javax.swing.text.SimpleAttributeSet();
            javax.swing.text.StyleConstants.setForeground(attrs, currentColor);
            javax.swing.text.StyleConstants.setBold(attrs, bold);
            try {
                textPane.getDocument().insertString(
                    textPane.getDocument().getLength(),
                    line + "\n", attrs
                );
                textPane.setCaretPosition(textPane.getDocument().getLength());
            } catch (javax.swing.text.BadLocationException e) {
                // Ignorar
            }
        }
    }
}
