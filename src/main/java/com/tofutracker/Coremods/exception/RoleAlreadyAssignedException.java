package com.tofutracker.Coremods.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class RoleAlreadyAssignedException extends RuntimeException {

    public RoleAlreadyAssignedException(String message) {
        super(message);
    }

    public RoleAlreadyAssignedException(String message, Throwable cause) {
        super(message, cause);
    }
} 