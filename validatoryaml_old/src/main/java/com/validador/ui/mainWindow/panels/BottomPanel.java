package com.validador.ui.mainWindow.panels;

import com.validador.ui.language.TextGestor;

import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JPanel {
    private JLabel authorLabel;
    private JLabel versionLabel;

    public BottomPanel(TextGestor textos, Color panelDark) {
        setBackground(panelDark);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        authorLabel = new JLabel();
        authorLabel.setText("<html><div style='text-align:center;'>" + textos.get("footer.author").replace("{author}", "<a href='mailto:" + textos.get("footer.mail") + "' style='color:#00adb5;text-decoration:none;'>Alejandro Sánchez Pinto</a>") + "</div></html>");
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        authorLabel.setForeground(new Color(200, 200, 200));
        authorLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        authorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        authorLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    Desktop.getDesktop().mail(new java.net.URI("mailto:" + textos.get("footer.mail")));
                } catch (Exception ex) {
                    // Ignorar
                }
            }
        });
        add(authorLabel);

        versionLabel = new JLabel("Versión 1.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(150, 150, 150));
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(versionLabel);
    }

    public void actualizarTextos(TextGestor textos) {
        authorLabel.setText("<html><div style='text-align:center;'>" + textos.get("footer.author").replace("{author}", "<a href='mailto:" + textos.get("footer.mail") + "' style='color:#00adb5;text-decoration:none;'>Alejandro Sánchez Pinto</a>") + "</div></html>");
        // La versión no cambia
    }
}
