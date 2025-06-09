package com.validador.ui.mainWindow.panels;

import com.validador.ui.language.TextGestor;

import javax.swing.*;
import java.awt.*;

public class OptionsPanel extends JPanel {
    public JButton validateButton;
    public JCheckBox exportSpectralCheck;
    public JTextField exportPathField;
    public JButton exportBrowseButton;

    public OptionsPanel(TextGestor textos, Color accent, Color bgDark, Color textColor, Color borderColor) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);
        validateButton = new JButton(textos.get("validate.button"));
        validateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        validateButton.setBackground(accent);
        validateButton.setForeground(bgDark);
        validateButton.setFocusPainted(false);
        validateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        validateButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        add(validateButton);
        add(Box.createHorizontalStrut(24));
        exportSpectralCheck = new JCheckBox(textos.get("export.spectral"));
        exportSpectralCheck.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportSpectralCheck.setBackground(bgDark);
        exportSpectralCheck.setForeground(textColor);
        add(exportSpectralCheck);
        exportPathField = new JTextField(18);
        exportPathField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        exportPathField.setBackground(bgDark);
        exportPathField.setForeground(textColor);
        exportPathField.setCaretColor(accent);
        exportPathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        exportPathField.setVisible(false);
        exportPathField.setEditable(false);
        exportPathField.setFocusable(false);
        add(exportPathField);
        exportBrowseButton = new JButton(textos.get("export.selectFolder"));
        exportBrowseButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exportBrowseButton.setBackground(accent);
        exportBrowseButton.setForeground(bgDark);
        exportBrowseButton.setFocusPainted(false);
        exportBrowseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exportBrowseButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        exportBrowseButton.setVisible(false);
        add(exportBrowseButton);
    }
    public void actualizarTextos(TextGestor textos) {
        validateButton.setText(textos.get("validate.button"));
        exportSpectralCheck.setText(textos.get("export.spectral"));
        exportBrowseButton.setText(textos.get("export.selectFolder"));
    }
}
