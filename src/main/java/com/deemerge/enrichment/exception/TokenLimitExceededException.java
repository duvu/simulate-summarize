package com.deemerge.enrichment.exception;

/**
 * Exception thrown when input text exceeds the maximum allowed tokens
 */
public class TokenLimitExceededException extends RuntimeException {

    public TokenLimitExceededException(int inputLength, int maxTokens) {
        super(String.format("Input text length (%d) exceeds the maximum allowed tokens (%d)", inputLength, maxTokens));
    }
}
