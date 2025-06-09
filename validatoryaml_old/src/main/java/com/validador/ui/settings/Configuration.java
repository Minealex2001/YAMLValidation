package com.validador.ui.settings;

import java.io.*;
import java.util.Properties;

public class Configuration {
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + "Documentos" + File.separator + "ValidadorYAML";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "configuracion.properties";
    private static final String SPECTRAL_PATH_KEY = "spectralPath";
    private static final String LANGUAGE_KEY = "language";

    private String spectralPath;
    private String language = "es";

    public Configuration() {
        cargarConfiguracion();
    }

    private void cargarConfiguracion() {
        try {
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(CONFIG_FILE);
            if (file.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(file)) {
                    props.load(fis);
                    spectralPath = props.getProperty(SPECTRAL_PATH_KEY, "");
                    language = props.getProperty(LANGUAGE_KEY, "es");
                }
            } else {
                spectralPath = "";
                language = "es";
            }
        } catch (Exception e) {
            spectralPath = "";
            language = "es";
        }
    }

    public boolean isSpectralPathSet() {
        return spectralPath != null && !spectralPath.trim().isEmpty();
    }

    public String getSpectralPath() {
        return spectralPath;
    }

    public void setSpectralPath(String path) {
        this.spectralPath = path;
        guardarConfiguracion();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String lang) {
        this.language = lang;
        guardarConfiguracion();
    }

    private void guardarConfiguracion() {
        try {
            Properties props = new Properties();
            props.setProperty(SPECTRAL_PATH_KEY, spectralPath);
            props.setProperty(LANGUAGE_KEY, language);
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) dir.mkdirs();
            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "Configuración Validador YAML");
            }
        } catch (Exception ignored) {}
    }
}
