package com.validador.validations.impl;

import com.validador.core.ValidationContext;
import com.validador.validations.ValidationLogger;
import com.validador.validations.ValidationRule;

import java.util.Map;

public class TypologyValidation implements ValidationRule {
    @Override
    public void validate(String endpoint, String method, ValidationContext context, ValidationLogger logger) {
        try {
            Map<String, Object> yamlData = context.getYamlData();
            Map<String, Object> paths = (Map<String, Object>) yamlData.get("paths");
            if (paths != null) {
                Map<String, Object> endpointObj = (Map<String, Object>) paths.get(endpoint);
                if (endpointObj != null) {
                    Map<String, Object> methodObj = (Map<String, Object>) endpointObj.get(method);
                    if (methodObj != null) {
                        Map<String, Object> typologyBlock = (Map<String, Object>) methodObj.get("x-typology");
                        if (typologyBlock != null) {
                            String typology = typologyBlock.getOrDefault("typology", "").toString();
                            if (!"external".equals(typology)) {
                                logger.log("ERROR", "x-typology.typology no es 'external': " + typology);
                            } else {
                                logger.log("INFO", "Validación exitosa: x-typology.typology es 'external'.");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log("ERROR", "Error en validarTypology: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "TypologyValidation";
    }
}
