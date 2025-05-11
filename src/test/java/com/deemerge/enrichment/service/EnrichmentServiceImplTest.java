package com.deemerge.enrichment.service;

import com.deemerge.enrichment.exception.EmptyInputException;
import com.deemerge.enrichment.exception.TenantNotFoundException;
import com.deemerge.enrichment.exception.TokenLimitExceededException;
import com.deemerge.enrichment.model.SummaryResponse;
import com.deemerge.enrichment.model.TenantSettings;
import com.deemerge.enrichment.repository.TenantSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrichmentServiceImplTest {

    @Mock
    private TenantSettingsRepository tenantSettingsRepository;

    @Mock
    private PromptBuilder promptBuilder;

    @InjectMocks
    private EnrichmentServiceImpl enrichmentService;

    private TenantSettings tenantSettings;
    private final String TENANT_ID = "test-tenant";
    private final String INPUT_TEXT = "This is a test input for summarization.";
    private final String PROMPT = "Please summarize the following text with a formal tone...";

    @BeforeEach
    void setUp() {
        tenantSettings = TenantSettings.builder()
                .model("gpt-4")
                .tone("formal")
                .maxTokens(300)
                .retryAttempts(3)
                .build();
    }

    @Test
    void summarize_WithValidInput_ReturnsSummaryResponse() {
        // Arrange
        when(tenantSettingsRepository.findByTenantId(TENANT_ID))
                .thenReturn(Optional.of(tenantSettings));
        // Mock the prompt builder - only needed for this test
        when(promptBuilder.buildSummarizationPrompt(any(TenantSettings.class), anyString()))
                .thenReturn(PROMPT);

        // Act
        SummaryResponse response = enrichmentService.summarize(TENANT_ID, INPUT_TEXT);

        // Assert
        assertNotNull(response);
        assertEquals(TENANT_ID, response.getTenantId());
        assertEquals(INPUT_TEXT, response.getInputText());
        assertNotNull(response.getSummary());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void summarize_WithUnknownTenant_ThrowsTenantNotFoundException() {
        // Arrange
        when(tenantSettingsRepository.findByTenantId("unknown-tenant"))
                .thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(TenantNotFoundException.class, () ->
                enrichmentService.summarize("unknown-tenant", INPUT_TEXT));
        
        assertTrue(exception.getMessage().contains("Unknown tenant"));
    }

    @Test
    void summarize_WithEmptyInput_ThrowsEmptyInputException() {
        // Act & Assert
        Exception exception = assertThrows(EmptyInputException.class, () ->
                enrichmentService.summarize(TENANT_ID, ""));
        
        assertTrue(exception.getMessage().contains("Input text cannot be empty"));
    }
    
    @Test
    void summarize_WithInputExceedingMaxTokens_ThrowsTokenLimitExceededException() {
        // Arrange
        when(tenantSettingsRepository.findByTenantId(TENANT_ID))
                .thenReturn(Optional.of(tenantSettings));
        
        // Create a string longer than the max tokens (300)
        StringBuilder longInput = new StringBuilder();
        for (int i = 0; i < 400; i++) { // exceeding 300 tokens limit
            longInput.append("a");
        }
        
        // Act & Assert
        Exception exception = assertThrows(TokenLimitExceededException.class, () ->
                enrichmentService.summarize(TENANT_ID, longInput.toString()));
        
        assertTrue(exception.getMessage().contains("exceeds the maximum allowed tokens"));
        assertTrue(exception.getMessage().contains("400"));
        assertTrue(exception.getMessage().contains("300"));
    }
}
