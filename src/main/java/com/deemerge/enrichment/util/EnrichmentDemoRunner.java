package com.deemerge.enrichment.util;

import com.deemerge.enrichment.model.SummaryResponse;
import com.deemerge.enrichment.service.EnrichmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Demo runner to showcase the enrichment service functionality
 * Only runs when the "demo" profile is active
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("demo")
public class EnrichmentDemoRunner implements CommandLineRunner {

    private final EnrichmentService enrichmentService;

    @Override
    public void run(String... args) {
        log.info("Starting Enrichment Service demo...");

        // Sample input text
        String sampleText = "The Enrichment Service is designed to provide AI-powered text summarization "
                + "for different tenants with varying configurations. It demonstrates how to structure "
                + "enrichment pipelines, handle prompts, simulate AI responses, implement retry logic, "
                + "and cache results efficiently. The service supports multiple tenants with different "
                + "settings and tones for summarization.";

        // Demo for tenant1 (formal)
        demonstrateForTenant("tenant1", sampleText);
        
        // Demo for tenant2 (friendly)
        demonstrateForTenant("tenant2", sampleText);
        
        // Demo for tenant3 (technical)
        demonstrateForTenant("tenant3", sampleText);
        
        // Demo error case
        MDC.put("tenantId", "unknown-tenant");
        MDC.put("requestId", UUID.randomUUID().toString());
        try {
            log.info("Demonstrating error case with unknown tenant");
            enrichmentService.summarize("unknown-tenant", sampleText);
        } catch (Exception e) {
            log.info("Expected error: {}", e.getMessage());
        } finally {
            MDC.clear();
        }

        log.info("Enrichment Service demo completed!");
    }
    
    private void demonstrateForTenant(String tenantId, String text) {
        // Set tenant ID in MDC for proper logging
        MDC.put("tenantId", tenantId);
        MDC.put("requestId", UUID.randomUUID().toString());
        
        try {
            log.info("Demonstrating summarization for tenant");
            
            // First call
            SummaryResponse response1 = enrichmentService.summarize(tenantId, text);
            log.info("First summary: {}", response1.getSummary());
            
            // Set a new request ID for the second call
            MDC.put("requestId", UUID.randomUUID().toString());
            
            // Second call (should be cached for the same input)
            SummaryResponse response2 = enrichmentService.summarize(tenantId, text);
            log.info("Second summary (from cache): {}", response2.getSummary());
            
        } catch (Exception e) {
            log.error("Error during demo: {}", e.getMessage());
        } finally {
            // Clear MDC after demonstration
            MDC.clear();
        }
    }
}
