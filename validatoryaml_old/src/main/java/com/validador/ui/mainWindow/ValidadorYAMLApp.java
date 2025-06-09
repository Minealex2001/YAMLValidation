package com.validador.ui.mainWindow;

import com.validador.core.ValidatorCore;
import com.validador.ui.language.TextGestor;
import com.validador.ui.mainWindow.panels.BottomPanel;
import com.validador.ui.mainWindow.panels.FilePanel;
import com.validador.ui.mainWindow.panels.OptionsPanel;
import com.validador.ui.mainWindow.panels.ResultsPanel;
import com.validador.ui.settings.Configuration;
import com.validador.validations.ValidationLogger;
import com.validador.validations.impl.SwingValidationLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ValidadorYAMLApp extends JFrame {
    private final FilePanel filePanel;
    private final OptionsPanel optionsPanel;
    private ResultsPanel resultsPanel;
    private final BottomPanel bottomPanel;
    private final transient TextGestor textos;
    private JMenu menuConfig;
    private final JMenu menuIdioma;
    private final JMenuItem cambiarRutaSpectral;
    private final JRadioButtonMenuItem esRadio;
    private final JRadioButtonMenuItem enRadio;
    private final JRadioButtonMenuItem caRadio;
    private final transient Configuration config;
    private final JTextPane appValidationOutput;
    private final JTextPane spectralValidationOutput;

    private static final String APP_TITLE = "app.title";
    private static final String MENU_CHANGE_SPECTRAL = "menu.changeSpectral";
    private static final String ERROR_NO_YAML = "error.noYaml";
    private static final String EXPORT_SPECTRAL = "export.spectral";
    private static final String VALIDATE_BUTTON = "validate.button";
    private static final String OUTPUT_EXPORT = "output.export";
    private static final String MENU_CONFIG = "menu.config";
    private static final String MENU_LANGUAGE = "menu.language";
    private static final String SPANISH = "Español";
    private static final String ENGLISH = "English";
    private static final String CATALAN = "Català";

    public ValidadorYAMLApp() {
        config = new Configuration();
        textos = new TextGestor(config.getLanguage());
        setTitle(textos.get(APP_TITLE));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel contentPane = new JPanel(new BorderLayout());
        Color bgDark = new Color(34, 40, 49);
        contentPane.setBackground(bgDark);
        contentPane.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(contentPane);

        resultsPanel = new ResultsPanel(textos);
        menuConfig = new JMenu(textos.get(MENU_CONFIG));
        cambiarRutaSpectral = new JMenuItem(textos.get(MENU_CHANGE_SPECTRAL));
        cambiarRutaSpectral.addActionListener(e -> pedirRutaSpectral());
        menuConfig.add(cambiarRutaSpectral);
        menuIdioma = new JMenu(textos.get(MENU_LANGUAGE));
        esRadio = new JRadioButtonMenuItem(SPANISH);
        enRadio = new JRadioButtonMenuItem(ENGLISH);
        caRadio = new JRadioButtonMenuItem(CATALAN);
        menuIdioma.add(esRadio);
        menuIdioma.add(enRadio);
        menuIdioma.add(caRadio);
        esRadio.setSelected(config.getLanguage().equals("es"));
        enRadio.setSelected(config.getLanguage().equals("en"));
        caRadio.setSelected(config.getLanguage().equals("ca"));
        esRadio.addActionListener(e -> cambiarIdioma("es"));
        enRadio.addActionListener(e -> cambiarIdioma("en"));
        caRadio.addActionListener(e -> cambiarIdioma("ca"));
        menuConfig.add(menuIdioma);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menuConfig);
        setJMenuBar(menuBar);

        // Inicialización de pestañas
        appValidationOutput = new JTextPane();
        spectralValidationOutput = new JTextPane();

        appValidationOutput.setEditable(false);
        spectralValidationOutput.setEditable(false);

        // Corrección para evitar agregar pestañas repetidas
        if (resultsPanel.getTabbedPane().getTabCount() == 0) {
            resultsPanel.getTabbedPane().addTab(textos.get("tab.appValidations"), new JScrollPane(appValidationOutput));
            resultsPanel.getTabbedPane().addTab(textos.get("tab.spectralValidations"), new JScrollPane(spectralValidationOutput));
        }

        // Panel central modular
        JPanel mainPanel = new JPanel();
        Color panelDark = new Color(44, 54, 63);
        mainPanel.setBackground(panelDark);
        mainPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Color accent = new Color(0, 173, 181);
        Color textColor = new Color(238, 238, 238);
        filePanel = new FilePanel(textos, accent, bgDark, textColor);
        Color borderColor = new Color(57, 62, 70);
        optionsPanel = new OptionsPanel(textos, accent, bgDark, textColor, borderColor);
        mainPanel.add(filePanel);
        mainPanel.add(Box.createVerticalStrut(16));
        mainPanel.add(optionsPanel);
        mainPanel.add(resultsPanel);
        contentPane.add(mainPanel, BorderLayout.CENTER);

        bottomPanel = new BottomPanel(textos, panelDark);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // Configurar listeners
        // Listeners de los paneles
        filePanel.browseButton.addActionListener(e -> chooseFile());
        optionsPanel.validateButton.addActionListener(e -> runValidation());
        optionsPanel.exportSpectralCheck.addActionListener(e -> {
            boolean selected = optionsPanel.exportSpectralCheck.isSelected();
            optionsPanel.exportBrowseButton.setVisible(selected);
            optionsPanel.exportPathField.setVisible(false);
            optionsPanel.revalidate();
            optionsPanel.repaint();
        });
        optionsPanel.exportBrowseButton.addActionListener(e -> chooseExportPath());

        // Corrección para mostrar resultados en la ventana principal y externa
        appValidationOutput.setText("Resultados de validación de la app");
        spectralValidationOutput.setText("Resultados de validación de Spectral");

        // Corrección para actualizar resultados en pestañas principales
        optionsPanel.validateButton.addActionListener(e -> {
            String yamlPath = filePanel.yamlPathLabel.getText().trim();
            resultsPanel.getAppValidationOutput().setText("");
            resultsPanel.getSpectralValidationOutput().setText("");
            if (yamlPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, textos.get(ERROR_NO_YAML), textos.get(VALIDATE_BUTTON), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Files.exists(Paths.get(yamlPath))) {
                JOptionPane.showMessageDialog(this, textos.get("error.noFile"), textos.get(VALIDATE_BUTTON), JOptionPane.ERROR_MESSAGE);
                return;
            }
            new Thread(() -> {
                try {
                    ValidationLogger logger = new SwingValidationLogger(resultsPanel.getAppValidationOutput());
                    ValidatorCore validator = new ValidatorCore(yamlPath, logger, config.getSpectralPath());
                    validator.runAllValidations(resultsPanel.getSpectralValidationOutput());
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> resultsPanel.getAppValidationOutput().setText("\nError: " + ex.getMessage()));
                }
            }).start();
        });

        JButton exportSpectralButton = new JButton(textos.get(EXPORT_SPECTRAL));
        exportSpectralButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(textos.get("export.selectFile"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                    writer.write(spectralValidationOutput.getText());
                    JOptionPane.showMessageDialog(this, textos.get("export.success"), textos.get(EXPORT_SPECTRAL), JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, textos.get("export.error"), textos.get(EXPORT_SPECTRAL), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        resultsPanel.add(exportSpectralButton, BorderLayout.SOUTH);

        JButton openResultsButton = new JButton(textos.get("output.openWindow"));
        openResultsButton.addActionListener(e -> abrirVentanaResultadosExternos());
        resultsPanel.add(openResultsButton, BorderLayout.SOUTH);
    }

    private void abrirVentanaResultadosExternos() {
        JFrame externalFrame = new JFrame(textos.get("export.dialogTitle"));
        externalFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        externalFrame.setSize(800, 600);
        externalFrame.setLocationRelativeTo(this);

        JTabbedPane externalTabbedPane = new JTabbedPane();
        JTextPane appResultsPane = new JTextPane();
        appResultsPane.setEditable(false);
        JTextPane spectralResultsPane = new JTextPane();
        spectralResultsPane.setEditable(false);

        // Copiar resultados de la pestaña principal con formato de color
        copyStyledDocument(resultsPanel.getAppValidationOutput(), appResultsPane);
        copyStyledDocument(resultsPanel.getSpectralValidationOutput(), spectralResultsPane);

        externalTabbedPane.addTab(textos.get("tab.appValidations"), new JScrollPane(appResultsPane));
        externalTabbedPane.addTab(textos.get("tab.spectralValidations"), new JScrollPane(spectralResultsPane));

        JButton exportSpectralButtonExternal = new JButton(textos.get(OUTPUT_EXPORT));
        exportSpectralButtonExternal.addActionListener(ev -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(textos.get("export.dialogTitle"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showSaveDialog(externalFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                    writer.write(spectralResultsPane.getText());
                    JOptionPane.showMessageDialog(externalFrame, textos.get("export.success"), textos.get(OUTPUT_EXPORT), JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(externalFrame, textos.get("export.error"), textos.get(OUTPUT_EXPORT), JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(exportSpectralButtonExternal);

        externalFrame.add(externalTabbedPane, BorderLayout.CENTER);
        externalFrame.add(buttonPanel, BorderLayout.SOUTH);
        externalFrame.setVisible(true);
    }

    private void copyStyledDocument(JTextPane from, JTextPane to) {
        to.setDocument(from.getStyledDocument());
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(textos.get("file.open"));
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos YAML (*.yaml, *.yml)", "yaml", "yml"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            filePanel.yamlPathLabel.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void chooseExportPath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(textos.get("export.selectFolder"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            optionsPanel.exportPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            optionsPanel.exportBrowseButton.setText(textos.get("export.folderSelected"));
        }
    }

    private void runValidation() {
        String yamlPath = filePanel.yamlPathLabel.getText().trim();
        appValidationOutput.setText("");
        spectralValidationOutput.setText("");
        if (yamlPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, textos.get(ERROR_NO_YAML), textos.get(VALIDATE_BUTTON), JOptionPane.ERROR_MESSAGE);
            appValidationOutput.setText(textos.get(ERROR_NO_YAML));
            return;
        }
        if (!Files.exists(Paths.get(yamlPath))) {
            appValidationOutput.setText(textos.get("error.noFile"));
            return;
        }
        try {
            Files.deleteIfExists(Paths.get("validacion.log"));
            Files.deleteIfExists(Paths.get("validacion_errores.log"));
        } catch (IOException e) {
            // Ignorar
        }
        boolean exportSpectral = optionsPanel.exportSpectralCheck.isSelected();
        String exportPath = optionsPanel.exportPathField.getText().trim();
        if (exportSpectral && exportPath.isEmpty()) {
            appValidationOutput.setText(textos.get("error.noExportPath"));
            return;
        }
        new Thread(() -> {
            try {
                ValidationLogger logger = new SwingValidationLogger(appValidationOutput);
                ValidatorCore validator = new ValidatorCore(yamlPath, logger, config.getSpectralPath());
                validator.runAllValidations(spectralValidationOutput);
                if (exportSpectral) {
                    validator.exportSpectralResult(exportPath);
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> appValidationOutput.setText("\nError: " + ex.getMessage()));
            }
        }).start();
    }

    private void pedirRutaSpectral() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(textos.get("spectral.selectFolder"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File spectralDir = chooser.getSelectedFile();
            config.setSpectralPath(spectralDir.getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(this, textos.get("spectral.required"), textos.get(MENU_CHANGE_SPECTRAL), JOptionPane.ERROR_MESSAGE);
            pedirRutaSpectral();
        }
    }

    private void cambiarIdioma(String idioma) {
        config.setLanguage(idioma);
        textos.setIdioma(idioma);
        setTitle(textos.get("app.title"));
        menuConfig.setText(textos.get("menu.config"));
        cambiarRutaSpectral.setText(textos.get(MENU_CHANGE_SPECTRAL));
        menuIdioma.setText(textos.get("menu.language"));
        esRadio.setText("Español");
        enRadio.setText("English");
        caRadio.setText("Català");
        filePanel.actualizarTextos(textos);
        optionsPanel.actualizarTextos(textos);
        resultsPanel.actualizarTextos(textos);
        bottomPanel.actualizarTextos(textos);
        repaint();
        revalidate();
    }
}
