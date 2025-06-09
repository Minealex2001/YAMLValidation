package com.validador.ui.mainWindow.panels;

import com.validador.ui.language.TextGestor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FilePanel extends JPanel {
    public JLabel yamlPathLabel;
    public JButton browseButton;

    public FilePanel(TextGestor textos, Color accent, Color bgDark, Color textColor) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel fileLabel = new JLabel(textos.get("file.label"));
        fileLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fileLabel.setForeground(accent);
        fileLabel.setBorder(new EmptyBorder(0, 0, 6, 0));
        fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(fileLabel);
        yamlPathLabel = new JLabel();
        yamlPathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        yamlPathLabel.setForeground(textColor);
        yamlPathLabel.setBorder(null);
        yamlPathLabel.setVerticalAlignment(SwingConstants.CENTER);
        yamlPathLabel.setHorizontalAlignment(SwingConstants.LEFT);
        yamlPathLabel.setPreferredSize(new Dimension(0, 32));
        yamlPathLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        yamlPathLabel.setMinimumSize(new Dimension(0, 32));
        yamlPathLabel.setOpaque(false);
        yamlPathLabel.setText("");
        JPanel fileLabelPanel = new JPanel();
        fileLabelPanel.setLayout(new BoxLayout(fileLabelPanel, BoxLayout.Y_AXIS));
        fileLabelPanel.setOpaque(false);
        fileLabelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fileLabelPanel.add(yamlPathLabel);
        fileLabelPanel.add(Box.createVerticalStrut(4));
        browseButton = new JButton(textos.get("file.open"));
        browseButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        browseButton.setBackground(accent);
        browseButton.setForeground(bgDark);
        browseButton.setFocusPainted(false);
        browseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        browseButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        fileLabelPanel.add(browseButton);
        add(fileLabelPanel);
    }
    public void actualizarTextos(TextGestor textos) {
        ((JLabel)getComponent(0)).setText(textos.get("file.label"));
        browseButton.setText(textos.get("file.open"));
    }
}

