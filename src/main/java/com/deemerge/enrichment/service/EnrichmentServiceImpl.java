package com.deemerge.enrichment.service;

import com.deemerge.enrichment.exception.EmptyInputException;
import com.deemerge.enrichment.exception.EnrichmentException;
import com.deemerge.enrichment.exception.TenantNotFoundException;
import com.deemerge.enrichment.exception.TokenLimitExceededException;
import com.deemerge.enrichment.model.SummaryResponse;
import com.deemerge.enrichment.model.TenantSettings;
import com.deemerge.enrichment.repository.TenantSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of EnrichmentService that simulates AI model responses
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EnrichmentServiceImpl implements EnrichmentService {

    private final TenantSettingsRepository tenantSettingsRepository;
    private final PromptBuilder promptBuilder;
    private final Random random = new Random();
    
    private static final double ERROR_RATE = 0.2; // 20% failure rate
    
    @Override
    @CachePut(value = "summaryCache", key = "#tenantId + '-' + #inputText.hashCode()")
    public SummaryResponse summarize(String tenantId, String inputText) {
        log.info("Summarization request received for tenant");
        
        if (inputText == null || inputText.trim().isEmpty()) {
            throw new EmptyInputException();
        }
        
        // Load tenant settings
        TenantSettings tenantSettings = tenantSettingsRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new TenantNotFoundException(tenantId));
        
        // Check if input text exceeds max tokens limit (assuming 1 character = 1 token)
        int inputLength = inputText.length();
        int maxTokens = tenantSettings.getMaxTokens();
        if (inputLength > maxTokens) {
            log.warn("Input text length ({}) exceeds maximum token limit ({}) for tenant {}", 
                    inputLength, maxTokens, tenantId);
            throw new TokenLimitExceededException(inputLength, maxTokens);
        }
        
        log.debug("Tenant settings loaded: {}", tenantSettings);
        
        // Build prompt
        String prompt = promptBuilder.buildSummarizationPrompt(tenantSettings, inputText);
        log.debug("Prompt built");
        
        try {
            // Simulate calling OpenAI with error handling and retry logic
            String summary = callOpenAIWithRetry(prompt, tenantSettings);
            
            // Create response
            SummaryResponse response = SummaryResponse.builder()
                    .inputText(inputText)
                    .summary(summary)
                    .tenantId(tenantId)
                    .timestamp(Instant.now())
                    .build();
            
            log.info("Summarization completed");
            return response;
            
        } catch (Exception e) {
            log.error("Error during summarization: {}", e.getMessage());
            throw new EnrichmentException("Failed to summarize text after retries", e);
        }
    }
    
    /**
     * Simulates calling OpenAI API with retry logic
     */
    private String callOpenAIWithRetry(String prompt, TenantSettings settings) throws InterruptedException {
        int attempts = 0;
        int maxAttempts = settings.getRetryAttempts();
        MDC.put("model", settings.getModel());
        
        while (attempts < maxAttempts) {
            attempts++;
            MDC.put("attemptNumber", String.valueOf(attempts));
            try {
                return simulateOpenAICall(prompt, settings);
            } catch (RuntimeException e) {
                log.warn("API call attempt failed: {}", e.getMessage());
                
                if (attempts >= maxAttempts) {
                    throw e;
                }
                
                // Exponential backoff
                TimeUnit.MILLISECONDS.sleep(100 * (long) Math.pow(2, attempts));
            }
        }
        
        throw new EnrichmentException("Failed after " + maxAttempts + " attempts");
    }
    
    /**
     * Simulates an OpenAI API call with random variations and failures
     */
    private String simulateOpenAICall(String prompt, TenantSettings settings) {
        try {
            int delay = settings.getModel().contains("4") ? 1000 : 500;
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate random failures (20% of calls)
        if (random.nextDouble() < ERROR_RATE) {
            throw new EnrichmentException("Simulated OpenAI API failure");
        }
        
        // Generate a simulated summary with some randomness
        return generateSimulatedSummary(prompt, settings);
    }
    
    /**
     * Generates a simulated summary based on the input prompt and settings
     */
    private String generateSimulatedSummary(String prompt, TenantSettings settings) {
        String[] lines = prompt.split("\n");
        StringBuilder summary = new StringBuilder("Summary: ");
        
        if (lines.length > 3) {
            int startLine = lines.length - Math.min(5, lines.length);
            for (int i = startLine; i < lines.length; i++) {
                if (lines[i].length() > 10) {
                    summary.append(lines[i].substring(0, Math.min(lines[i].length(), 30)));
                    summary.append("... ");
                }
            }
        } else {
            summary.append("This is a simulated summary for ").append(settings.getModel());
        }
        
        // Add variation based on tenant tone
        if (settings.getTone().equals("formal")) {
            summary.append("In conclusion, this summarizes the key points.");
        } else if (settings.getTone().equals("friendly")) {
            summary.append("Hope this helps you understand the main ideas!");
        } else if (settings.getTone().equals("technical")) {
            summary.append("Technical analysis complete. Key findings documented above.");
        }
        
        return summary.toString();
    }
}
