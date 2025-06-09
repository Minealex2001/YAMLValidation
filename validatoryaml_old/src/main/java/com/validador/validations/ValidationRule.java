package com.validador.validations;

import com.validador.core.ValidationContext;

public interface ValidationRule {
    void validate(String endpoint, String method, ValidationContext context, ValidationLogger logger);
    String getName();
}
