package com.tofutracker.Coremods.exception;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("jakarta.servlet.error.exception");

        if (statusCode == null) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        
        HttpStatus status = HttpStatus.valueOf(statusCode);
        String message = switch (status) {
            case NOT_FOUND -> "Resource not found";
            case FORBIDDEN -> "Access denied";
            case UNAUTHORIZED -> "Authentication required";
            case BAD_REQUEST -> "Invalid request";
            case INTERNAL_SERVER_ERROR -> "An unexpected error occurred";
            default -> status.getReasonPhrase();
        };

        if (exception != null && exception.getMessage() != null && !exception.getMessage().isEmpty()) {
            message = exception.getMessage();
        }
        
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(message));
    }
} 