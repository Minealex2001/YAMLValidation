package com.validador.validations.impl;

import com.validador.core.ValidationContext;
import com.validador.validations.ValidationLogger;
import com.validador.validations.ValidationRule;

import java.util.Map;

public class Codigo400Validation implements ValidationRule {
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
                        Map<String, Object> responses = (Map<String, Object>) methodObj.get("responses");
                        if (responses != null) {
                            Map<String, Object> resp400 = (Map<String, Object>) responses.get("400");
                            String description = resp400 != null ? resp400.getOrDefault("description", "").toString() : "";
                            Map<String, Object> info = (Map<String, Object>) yamlData.get("info");
                            Map<String, Object> xFuncDomains = info != null ? (Map<String, Object>) info.get("x-functional-domains") : null;
                            String domain = xFuncDomains != null ? xFuncDomains.getOrDefault("domain", "").toString().toUpperCase() : "";
                            if (!description.isEmpty()) {
                                if (description.contains(domain + "/")) {
                                    logger.log("INFO", "Validación exitosa: La descripción del código '400' contiene el dominio en mayúsculas: " + domain + ".");
                                } else {
                                    logger.log("ERROR", "La descripción del código '400' no contiene el dominio en mayúsculas: " + domain + ". Descripción: " + description);
                                }
                            } else {
                                logger.log("WARNING", "La descripción del código '400' no está definida para el endpoint '" + endpoint + "', método '" + method + "'.");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log("ERROR", "Error en validarCodigo400: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "Codigo400Validation";
    }
}
