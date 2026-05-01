package com.jasper.config;

import com.jasper.common.CommonResponse;
import com.jasper.common.CustomException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler
 * Converts exceptions to CommonResponse format
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        
        log.warn("Validation error: {}", message);
        
        CommonResponse<Void> response = CommonResponse.failure(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getMessage();
        log.warn("Constraint violation: {}", message);
        
        CommonResponse<Void> response = CommonResponse.failure(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        
        CommonResponse<Void> response = CommonResponse.failure(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle custom business exceptions
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<Void>> handleCustomException(CustomException ex) {
        log.warn("Custom exception [{}]: {}", ex.getErrorCode(), ex.getMessage());
        
        CommonResponse<Void> response = CommonResponse.failure(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getErrorCode()).body(response);
    }

    /**
     * Handle runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception", ex);
        
        CommonResponse<Void> response = CommonResponse.failure(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "Internal server error: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception ex) {
        log.error("Unexpected exception", ex);
        
        CommonResponse<Void> response = CommonResponse.failure(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "Unexpected error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
