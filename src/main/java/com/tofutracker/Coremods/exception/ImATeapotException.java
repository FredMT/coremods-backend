package com.tofutracker.Coremods.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
public class ImATeapotException extends RuntimeException {

    public ImATeapotException(String message) {
        super(message);
    }

    public ImATeapotException(String message, Throwable cause) {
        super(message, cause);
    }
}