package com.validador.validations.impl;

import com.validador.core.ValidationContext;
import com.validador.validations.ValidationLogger;
import com.validador.validations.ValidationRule;

import java.util.Map;

public class OperationIdValidation implements ValidationRule {
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
                        String operationId = methodObj.getOrDefault("operationId", "").toString();
                        if (!endpoint.contains("/int")) {
                            if (operationId.startsWith("internal")) {
                                logger.log("ERROR", "operationId no debe llevar prefijo 'internal' para endpoints que no contienen '/int'");
                            }
                        }
                        if (operationId.isEmpty()) {
                            logger.log("WARNING", "operationId no está definido para endpoint.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log("ERROR", "Error en validarOperationId: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "OperationIdValidation";
    }
}
