package com.tofutracker.Coremods.exception;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(RoleAlreadyAssignedException.class)
        public ResponseEntity<ApiResponse<Void>> handleRoleAlreadyAssignedException(RoleAlreadyAssignedException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(UnauthorizedException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiResponse<Void>> handleForbiddenException(ForbiddenException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error("Access denied: " + ex.getMessage()));
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException() {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid credentials"));
        }

        @ExceptionHandler(DisabledException.class)
        public ResponseEntity<ApiResponse<Void>> handleDisabledException() {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Account is disabled"));
        }

        @ExceptionHandler(LockedException.class)
        public ResponseEntity<ApiResponse<Void>> handleLockedException() {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Account is locked"));
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error("Authentication failed: " + ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Validation failed", errors));
        }

        @ExceptionHandler(BindException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleBindExceptions(BindException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Validation failed", errors));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
                        ConstraintViolationException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getConstraintViolations().forEach(violation -> errors.put(violation.getPropertyPath().toString(),
                                violation.getMessage()));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Validation failed", errors));
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiResponse<Void>> handleMissingParams(MissingServletRequestParameterException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Missing parameter: " + ex.getParameterName()));
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(
                        MethodArgumentTypeMismatchException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Invalid parameter value: " + ex.getName()));
        }

        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error("Resource not found: " + ex.getRequestURL()));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException() {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(
                                "File size exceeds maximum allowed size. Header images must be under 750KB, mod images must be under 8MB."));
        }

        @ExceptionHandler(HttpMessageNotWritableException.class)
        public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotWritableException() {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("Unable to serialize response data"));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleAllUncaughtException() {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("An unexpected error occurred"));
        }

        @ExceptionHandler(ImATeapotException.class)
        public ResponseEntity<ApiResponse<Void>> handleImATeapotException(ImATeapotException ex) {
                return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(ApiResponse.error(ex.getMessage()));
        }
}