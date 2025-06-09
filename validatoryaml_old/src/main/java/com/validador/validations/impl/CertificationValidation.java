package com.validador.validations.impl;

import com.validador.core.ValidationContext;
import com.validador.validations.ValidationLogger;
import com.validador.validations.ValidationRule;

import java.util.Map;

public class CertificationValidation implements ValidationRule {
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
                        Map<String, Object> certBlock = (Map<String, Object>) methodObj.get("x-certification");
                        if (certBlock != null) {
                            String certification = certBlock.getOrDefault("certification", "").toString();
                            String objective = certBlock.getOrDefault("objective", "").toString();
                            String year = certBlock.getOrDefault("year", "").toString();
                            if (!certification.isEmpty() && "A".equals(certification)) {
                                logger.log("WARNING", "x-certification.certification tiene el valor 'A'. Debería estar vacío si es la primera vez que se presenta a API Team CXB.");
                            }
                            if (!"A".equals(objective)) {
                                logger.log("ERROR", "x-certification.objective debería ser 'A' la primera vez que se presenta. Valor actual: " + objective);
                            } else {
                                logger.log("INFO", "Validación exitosa: x-certification.objective es 'A'.");
                            }
                            if (!"2025".equals(year)) {
                                logger.log("ERROR", "x-certification.year no es '2025': " + year);
                            } else {
                                logger.log("INFO", "Validación exitosa: x-certification.year es '2025'.");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log("ERROR", "Error en validarCertification: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "CertificationValidation";
    }
}
