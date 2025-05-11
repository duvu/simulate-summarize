package com.deemerge.enrichment.exception;

/**
 * Exception thrown when enrichment processing fails
 */
public class EnrichmentException extends RuntimeException {

    public EnrichmentException(String message) {
        super(message);
    }

    public EnrichmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
