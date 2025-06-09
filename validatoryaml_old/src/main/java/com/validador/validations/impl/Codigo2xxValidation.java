package com.validador.validations.impl;

import com.validador.core.ValidationContext;
import com.validador.validations.ValidationLogger;
import com.validador.validations.ValidationRule;

import java.util.Collections;
import java.util.Map;

public class Codigo2xxValidation implements ValidationRule {
    @Override
    public void validate(String endpoint, String method, ValidationContext context, ValidationLogger logger) {
        try {
            boolean found2xx = false;
            String[] codes = {"200", "201", "202", "204"};
            Map<String, Object> paths = context.getYamlData().get("paths") instanceof Map ? (Map<String, Object>) context.getYamlData().get("paths") : Collections.emptyMap();
            Map<String, Object> endpointObj = paths.get(endpoint) instanceof Map ? (Map<String, Object>) paths.get(endpoint) : Collections.emptyMap();
            Map<String, Object> methodObj = endpointObj.get(method) instanceof Map ? (Map<String, Object>) endpointObj.get(method) : Collections.emptyMap();
            Map<String, Object> responses = methodObj.get("responses") instanceof Map ? (Map<String, Object>) methodObj.get("responses") : Collections.emptyMap();
            for (String code : codes) {
                Object resp = responses.get(code);
                if (resp != null) {
                    found2xx = true;
                    break;
                }
            }
            if (found2xx) {
                logger.log("INFO", "Validación exitosa: El endpoint '" + endpoint + "' con método '" + method + "' tiene al menos un código de respuesta 2xx.");
            } else {
                logger.log("ERROR", "El endpoint '" + endpoint + "' con método '" + method + "' no tiene ningún código de respuesta 2xx (200, 201, 202, 204).");
            }
        } catch (Exception e) {
            logger.log("ERROR", "Error en validarCodigo2xx: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "Codigo2xxValidation";
    }
}
