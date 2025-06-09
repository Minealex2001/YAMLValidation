package com.validador.core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import com.validador.validations.ValidationLogger;
import com.validador.validations.ValidationRule;
import com.validador.validations.impl.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.LoaderOptions;

import javax.swing.*;

public class ValidatorCore {
    private final String yamlPath;
    private final ValidationLogger logger;
    private final String spectralPath;
    private final List<ValidationRule> rules = new ArrayList<>();
    private Map<String, Object> yamlData;
    private String spectralOutput = "";

    private static final String ERROR = "ERROR";
    private static final String WARNING = "WARNING";
    private static final String INFO = "INFO";

    public ValidatorCore(String yamlPath, ValidationLogger logger, String spectralPath) {
        this.yamlPath = yamlPath;
        this.logger = logger;
        this.spectralPath = spectralPath;
        // Cargar YAML en memoria
        try (InputStream input = Files.newInputStream(Paths.get(yamlPath))) {
            Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
            this.yamlData = yaml.load(input);
        } catch (IOException e) {
            logger.log(ERROR, "No se pudo cargar el archivo YAML: " + e.getMessage());
            this.yamlData = new HashMap<>();
        }

        // Registrar reglas de validación aquí
        rules.add(new OperationIdValidation());
        rules.add(new AbsisOperationValidation());
        rules.add(new CertificationValidation());
        rules.add(new TypologyValidation());
        rules.add(new Codigo2xxValidation());
        rules.add(new RequestBodyValidation());
        rules.add(new Codigo400Validation());
    }

    private void log(String level, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:").format(new Date());
        String logLine = timestamp + " [" + level + "] " + message;
        // Crear carpeta log si no existe
        File logDir = new File("log");
        if (!logDir.exists() && !logDir.mkdirs()) {
            logger.log(ERROR, "No se pudo crear el directorio de logs.");
        }
        try (FileWriter fw = new FileWriter("log/validacion.log", true)) {
            if (level.equals(INFO) || level.isEmpty()) {
                fw.write(logLine + "\n");
            }
        } catch (IOException e) {
            // Error escribiendo log general
        }
        try (FileWriter fw = new FileWriter("log/validacion_errores.log", true)) {
            if (level.equals(WARNING) || level.equals(ERROR) || level.isEmpty()) {
                fw.write(logLine + "\n");
            }
        } catch (IOException e) {
            // Error escribiendo log de errores
        }
        // Mostrar SIEMPRE el log en pantalla, sin importar el nivel
        logger.log(level, message);
    }

    // Obtener lista de endpoints (keys de paths)
    private List<String> getEndpoints() {
        Object pathsObj = yamlData.get("paths");
        if (pathsObj instanceof Map) {
            return new ArrayList<>(((Map<?, ?>) pathsObj).keySet()).stream().map(Object::toString).collect(java.util.stream.Collectors.toList());
        }
        return Collections.emptyList();
    }

    // Obtener lista de métodos para un endpoint
    private List<String> getMethods(String endpoint) {
        Object pathsObj = yamlData.get("paths");
        if (pathsObj instanceof Map) {
            Object endpointObj = ((Map<?, ?>) pathsObj).get(endpoint);
            if (endpointObj instanceof Map) {
                return new ArrayList<>(((Map<?, ?>) endpointObj).keySet()).stream().map(Object::toString).collect(java.util.stream.Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    public void runAllValidations(JTextPane spectralPane) {
        Thread spectralThread = new Thread(() -> runSpectral(spectralPane));
        spectralThread.start();
        try {
            List<String> endpoints = getEndpoints();
            for (String endpoint : endpoints) {
                List<String> methods = getMethods(endpoint);
                for (String method : methods) {
                    logger.log("", "**********Validando endpoint: " + endpoint + ", método: " + method + "*********");
                    ValidationContext context = new ValidationContext(yamlPath, this);
                    for (ValidationRule rule : rules) {
                        rule.validate(endpoint, method, context, logger);
                    }
                }
            }
            boolean hasWarningsOrErrors = hasWarningsOrErrors();
            if (hasWarningsOrErrors) {
                log(ERROR, "Se encontraron warnings o errores durante la validación.");
            } else {
                log(INFO, "Validación completa sin warnings ni errores.");
            }
            spectralThread.join(); // Esperar a que termine Spectral antes de continuar
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log(ERROR, "Error en la validación: " + e.getMessage());
        } catch (Exception e) {
            // No se puede re-interrumpir aquí, solo loguear
            log(ERROR, "Error en la validación: " + e.getMessage());
        }
    }

    public void runSpectral(JTextPane spectralPane) {
        try {
            StringBuilder outputBuilder = new StringBuilder();
            ProcessBuilder pb = new ProcessBuilder(
                "cmd", "/c", "spectral lint -r ./poc/.spectral_v2.yaml -f pretty \"" + yamlPath + "\""
            );
            pb.directory(new java.io.File(spectralPath));
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }
            proc.waitFor();
            spectralOutput = outputBuilder.toString();
            // Mostrar toda la salida con el color adecuado por bloque
            SwingUtilities.invokeLater(() -> {
                spectralPane.setText("");
                com.validador.ui.mainWindow.panels.ResultsPanel.appendSpectralStyledBlock(spectralPane, spectralOutput);
            });
            log(INFO, "Validación Spectral finalizada.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log(ERROR, "Error al ejecutar Spectral: " + e.getMessage());
        } catch (Exception e) {
            // No se puede re-interrumpir aquí, solo loguear
            log(ERROR, "Error al ejecutar Spectral: " + e.getMessage());
        }
    }

    public void exportSpectralResult(String exportPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "cmd", "/c", "spectral lint -r ./poc/.spectral_v2.yaml -f pretty \"" + yamlPath + "\""
            );
            pb.directory(new java.io.File(spectralPath));
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportPath))) {
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            proc.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log(ERROR, "Error al exportar resultado Spectral: " + e.getMessage());
        } catch (Exception e) {
            // No se puede re-interrumpir aquí, solo loguear
            log(ERROR, "Error al exportar resultado Spectral: " + e.getMessage());
        }
    }

    private boolean hasWarningsOrErrors() {
        try (BufferedReader br = new BufferedReader(new FileReader("log/validacion_errores.log"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("WARNING") || line.contains("ERROR")) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    public Map<String, Object> getYamlData() {
        return yamlData;
    }
}
