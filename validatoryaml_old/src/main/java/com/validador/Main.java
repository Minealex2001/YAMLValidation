package com.validador;

import com.validador.ui.mainWindow.ValidadorYAMLApp;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el look and feel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            ValidadorYAMLApp app = new ValidadorYAMLApp();
            app.setVisible(true);
        });
    }
}
