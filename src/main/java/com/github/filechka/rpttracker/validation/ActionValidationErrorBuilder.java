package com.github.filechka.rpttracker.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class ActionValidationErrorBuilder {
    public static ActionValidationError fromBindingErrors(Errors errors) {
        ActionValidationError error = new ActionValidationError("Action validation failed. "
                + errors.getErrorCount() + " error(s)");
        for (ObjectError objectError: errors.getAllErrors()) {
            error.addValidationError(objectError.getDefaultMessage());
        }
        return error;
    }
}
