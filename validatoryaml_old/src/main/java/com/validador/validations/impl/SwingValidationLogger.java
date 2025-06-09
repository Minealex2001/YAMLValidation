package com.validador.validations.impl;

import com.validador.validations.ValidationLogger;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class SwingValidationLogger implements ValidationLogger {
    private final JTextPane outputArea;

    public SwingValidationLogger(JTextPane outputArea) {
        this.outputArea = outputArea;
    }

    @Override
    public void log(String level, String message) {
        SwingUtilities.invokeLater(() -> {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            String prefix = level.isEmpty() ? "" : ("[" + level + "] ");
            // Colores según el tipo de mensaje
            switch (level.toUpperCase()) {
                case "ERROR":
                    StyleConstants.setForeground(attrs, new Color(255, 85, 85));
                    StyleConstants.setBold(attrs, true);
                    break;
                case "WARNING":
                case "WARN":
                    StyleConstants.setForeground(attrs, new Color(255, 180, 60));
                    StyleConstants.setBold(attrs, true);
                    break;
                case "SUCCESS":
                case "OK":
                    StyleConstants.setForeground(attrs, new Color(80, 220, 120));
                    StyleConstants.setBold(attrs, true);
                    break;
                case "INFO":
                    StyleConstants.setForeground(attrs, new Color(0, 173, 181));
                    break;
                case "SPECTRAL":
                    StyleConstants.setForeground(attrs, new Color(180, 180, 255));
                    break;
                default:
                    StyleConstants.setForeground(attrs, new Color(238, 238, 238));
            }
            try {
                outputArea.getDocument().insertString(
                    outputArea.getDocument().getLength(),
                    prefix + message + "\n", attrs
                );
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            } catch (BadLocationException e) {
                // Ignorar
            }
        });
    }
}
