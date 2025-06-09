package com.validador.validations.impl;

import com.validador.core.ValidationContext;
import com.validador.validations.ValidationLogger;
import com.validador.validations.ValidationRule;

import java.util.Map;

public class RequestBodyValidation implements ValidationRule {
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
                        Object requestBody = methodObj.get("requestBody");
                        if (method.equalsIgnoreCase("post") || method.equalsIgnoreCase("put")) {
                            if (requestBody == null) {
                                logger.log("ERROR", "requestBody no está definido para el método '" + method + "'.");
                            } else {
                                logger.log("INFO", "Validación exitosa: requestBody está definido para el método '" + method + "' en el endpoint '" + endpoint + "'.");
                            }
                        } else {
                            if (requestBody != null) {
                                logger.log("ERROR", "requestBody está definido para el método '" + method + "', pero solo debería estar presente para métodos POST o PUT.");
                            } else {
                                logger.log("INFO", "Validación exitosa: requestBody no está definido para el método '" + method + "' en el endpoint '" + endpoint + "'.");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log("ERROR", "Error en validarRequestBody: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "RequestBodyValidation";
    }
}
