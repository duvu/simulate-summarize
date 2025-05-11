package com.deemerge.enrichment.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles TenantNotFoundException and returns a 404 Not Found response
     */
    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTenantNotFoundException(TenantNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("TENANT_NOT_FOUND", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
    
    /**
     * Handles EmptyInputException and returns a 400 Bad Request response
     */
    @ExceptionHandler(EmptyInputException.class)
    public ResponseEntity<ErrorResponse> handleEmptyInputException(EmptyInputException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("EMPTY_INPUT", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
    
    /**
     * Handles TokenLimitExceededException and returns a 400 Bad Request response
     */
    @ExceptionHandler(TokenLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleTokenLimitExceededException(TokenLimitExceededException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("TOKEN_LIMIT_EXCEEDED", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
    
    /**
     * Handles EnrichmentException and returns a 500 Internal Server Error response
     */
    @ExceptionHandler(EnrichmentException.class)
    public ResponseEntity<ErrorResponse> handleEnrichmentException(EnrichmentException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("ENRICHMENT_ERROR", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Handles IllegalArgumentException and returns a 400 Bad Request response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("BAD_REQUEST", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handles RuntimeException and returns a 500 Internal Server Error response
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Error response DTO
     */
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String code;
        private String message;
    }
}
