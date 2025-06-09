package com.validador.validations.impl;

import com.validador.core.ValidationContext;
import com.validador.validations.ValidationLogger;
import com.validador.validations.ValidationRule;

import java.util.Map;

public class AbsisOperationValidation implements ValidationRule {
    @Override
    public void validate(String endpoint, String method, ValidationContext context, ValidationLogger logger) {
        try {
            // Acceso directo al YAML usando SnakeYAML
            Map<String, Object> yamlData = context.getYamlData();
            Map<String, Object> paths = (Map<String, Object>) yamlData.get("paths");
            if (paths != null) {
                Map<String, Object> endpointObj = (Map<String, Object>) paths.get(endpoint);
                if (endpointObj != null) {
                    Map<String, Object> methodObj = (Map<String, Object>) endpointObj.get(method);
                    if (methodObj != null) {
                        Map<String, Object> absisOp = (Map<String, Object>) methodObj.get("x-absis-operation");
                        if (absisOp != null) {
                            String type = absisOp.getOrDefault("type", "").toString();
                            String security = absisOp.getOrDefault("security", "").toString();
                            Map<String, Object> info = (Map<String, Object>) yamlData.get("info");
                            String title = info != null ? info.getOrDefault("title", "").toString() : "";
                            String operationId = methodObj.getOrDefault("operationId", "").toString();
                            if (method.equalsIgnoreCase("get")) {
                                if ("informational".equals(type)) {
                                    logger.log("INFO", "Validación exitosa: x-absis-operation.type es 'informational' para método GET.");
                                } else {
                                    logger.log("ERROR", "x-absis-operation.type valor actual: " + type + ", valor esperado 'informational'");
                                }
                            } else if (method.equalsIgnoreCase("post") || method.equalsIgnoreCase("put")) {
                                if ("management".equals(type)) {
                                    logger.log("INFO", "Validación exitosa: x-absis-operation.type es 'management' para método POST o PUT.");
                                } else {
                                    logger.log("ERROR", "x-absis-operation.type valor actual: " + type + " , valor esperado: 'management'");
                                }
                            }
                            if (!security.isEmpty()) {
                                String expected = title + "." + operationId;
                                if (security.equals(expected)) {
                                    logger.log("INFO", "Validación exitosa: x-absis-operation.security tiene el formato correcto.");
                                } else {
                                    logger.log("ERROR", "x-absis-operation.security no tiene el formato correcto: " + security + " (se esperaba: " + expected + ")");
                                }
                            } else {
                                logger.log("ERROR", "x-absis-operation.security no está definido.");
                            }
                        } else {
                            logger.log("ERROR", "x-absis-operation no está definido.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log("ERROR", "Error en validarAbsisOperation: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "AbsisOperationValidation";
    }
}
