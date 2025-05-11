package com.deemerge.enrichment.service;

import com.deemerge.enrichment.model.SummaryResponse;

/**
 * Service to handle text enrichment operations including summarization
 */
public interface EnrichmentService {
    
    /**
     * Summarizes the provided input text based on tenant-specific settings
     * 
     * @param tenantId The unique identifier of the tenant
     * @param inputText The text to be summarized
     * @return SummaryResponse containing the summarized text and metadata
     */
    SummaryResponse summarize(String tenantId, String inputText);
}
