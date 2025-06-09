package com.validador.core;

import java.util.Map;

public class ValidationContext {
    private final String yamlPath;
    private final ValidatorCore core;

    public ValidationContext(String yamlPath, ValidatorCore core) {
        this.yamlPath = yamlPath;
        this.core = core;
    }

    public String getYamlPath() {
        return yamlPath;
    }

    public Map<String, Object> getYamlData() {
        return core.getYamlData();
    }
}
