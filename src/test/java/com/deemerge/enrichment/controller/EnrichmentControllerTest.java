package com.deemerge.enrichment.controller;

import com.deemerge.enrichment.exception.EmptyInputException;
import com.deemerge.enrichment.model.SummarizationRequest;
import com.deemerge.enrichment.model.SummaryResponse;
import com.deemerge.enrichment.service.EnrichmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrichmentControllerTest {

    @Mock
    private EnrichmentService enrichmentService;

    @InjectMocks
    private EnrichmentController enrichmentController;

    private final String TENANT_ID = "test-tenant";
    private final String INPUT_TEXT = "Test input text for summarization";
    private final String SUMMARY = "This is a summarized version of the input text";
    private SummaryResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = SummaryResponse.builder()
                .tenantId(TENANT_ID)
                .inputText(INPUT_TEXT)
                .summary(SUMMARY)
                .timestamp(Instant.now())
                .build();
    }

    @Test
    void summarize_ReturnsOkResponseWithSummary() {
        // Arrange
        when(enrichmentService.summarize(eq(TENANT_ID), anyString()))
                .thenReturn(mockResponse);
                
        SummarizationRequest request = new SummarizationRequest(INPUT_TEXT);

        // Act
        ResponseEntity<SummaryResponse> response = 
                enrichmentController.summarize(TENANT_ID, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TENANT_ID, response.getBody().getTenantId());
        assertEquals(INPUT_TEXT, response.getBody().getInputText());
        assertEquals(SUMMARY, response.getBody().getSummary());
    }

    @Test
    void summarize_WithNullInputText_ThrowsEmptyInputException() {
        // Arrange
        SummarizationRequest request = new SummarizationRequest(null);

        // Act & Assert
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
            EmptyInputException.class, 
            () -> enrichmentController.summarize(TENANT_ID, request)
        );
        
        assertEquals("Input text cannot be empty", exception.getMessage());
    }
}
