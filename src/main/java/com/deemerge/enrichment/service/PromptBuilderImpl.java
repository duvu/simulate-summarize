package com.deemerge.enrichment.service;

import com.deemerge.enrichment.model.TenantSettings;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of PromptBuilder that constructs prompts based on tenant settings
 */
@Service
public class PromptBuilderImpl implements PromptBuilder {

    // Templates for different tones
    private final Map<String, String> toneTemplates = new HashMap<>();
    
    // Base template for summarization
    private static final String BASE_TEMPLATE = "Please summarize the following text. %s\n\nText to summarize:\n%s";
    
    public PromptBuilderImpl() {
        // Initialize tone templates
        toneTemplates.put("formal", "Use a professional and formal tone in your response.");
        toneTemplates.put("friendly", "Use a casual and conversational tone in your response.");
        toneTemplates.put("technical", "Use technical language and focus on key technical details in your response.");
    }
    
    @Override
    public String buildSummarizationPrompt(TenantSettings tenantSettings, String inputText) {
        // Get the appropriate tone instructions or default to empty string
        String toneInstructions = toneTemplates.getOrDefault(tenantSettings.getTone(), "");
        
        // Build the complete prompt
        return String.format(BASE_TEMPLATE, toneInstructions, inputText);
    }
}
