package com.deemerge.enrichment.service;

import com.deemerge.enrichment.model.TenantSettings;

/**
 * PromptBuilder interface for constructing prompts based on tenant settings
 */
public interface PromptBuilder {
    
    /**
     * Builds a prompt for summarization based on tenant settings and input text
     * 
     * @param tenantSettings The settings specific to the tenant
     * @param inputText The text to be summarized
     * @return A complete prompt for the AI model
     */
    String buildSummarizationPrompt(TenantSettings tenantSettings, String inputText);
}
