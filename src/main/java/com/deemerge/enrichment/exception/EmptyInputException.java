package com.deemerge.enrichment.exception;

/**
 * Exception thrown when input text for summarization is empty or null
 */
public class EmptyInputException extends RuntimeException {

    public EmptyInputException() {
        super("Input text cannot be empty");
    }
}
