package com.deemerge.enrichment.service;

import com.deemerge.enrichment.model.TenantSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptBuilderImplTest {

    private PromptBuilderImpl promptBuilder;
    private final String INPUT_TEXT = "This is a test text for summarization.";

    @BeforeEach
    void setUp() {
        promptBuilder = new PromptBuilderImpl();
    }

    @Test
    void buildSummarizationPrompt_WithFormalTone_ReturnsCorrectPrompt() {
        // Arrange
        TenantSettings settings = TenantSettings.builder()
                .tone("formal")
                .model("gpt-4")
                .maxTokens(300)
                .build();

        // Act
        String result = promptBuilder.buildSummarizationPrompt(settings, INPUT_TEXT);

        // Assert
        assertTrue(result.contains("professional and formal tone"));
        assertTrue(result.contains(INPUT_TEXT));
    }

    @Test
    void buildSummarizationPrompt_WithFriendlyTone_ReturnsCorrectPrompt() {
        // Arrange
        TenantSettings settings = TenantSettings.builder()
                .tone("friendly")
                .model("gpt-3.5")
                .maxTokens(200)
                .build();

        // Act
        String result = promptBuilder.buildSummarizationPrompt(settings, INPUT_TEXT);

        // Assert
        assertTrue(result.contains("casual and conversational tone"));
        assertTrue(result.contains(INPUT_TEXT));
    }

    @Test
    void buildSummarizationPrompt_WithTechnicalTone_ReturnsCorrectPrompt() {
        // Arrange
        TenantSettings settings = TenantSettings.builder()
                .tone("technical")
                .model("gpt-4")
                .maxTokens(250)
                .build();

        // Act
        String result = promptBuilder.buildSummarizationPrompt(settings, INPUT_TEXT);

        // Assert
        assertTrue(result.contains("technical language"));
        assertTrue(result.contains(INPUT_TEXT));
    }

    @Test
    void buildSummarizationPrompt_WithUnknownTone_ReturnsDefaultPrompt() {
        // Arrange
        TenantSettings settings = TenantSettings.builder()
                .tone("unknown")
                .model("gpt-3.5")
                .maxTokens(200)
                .build();

        // Act
        String result = promptBuilder.buildSummarizationPrompt(settings, INPUT_TEXT);

        // Assert
        assertTrue(result.contains("Please summarize the following text."));
        assertTrue(result.contains(INPUT_TEXT));
    }
}
