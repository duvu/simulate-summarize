package com.deemerge.enrichment;

import com.deemerge.enrichment.model.SummarizationRequest;
import com.deemerge.enrichment.model.SummaryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EnrichmentServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void summarizeEndpoint_WithValidInput_ReturnsSummaryResponse() {
        // Arrange
        String tenantId = "tenant1";
        String inputText = "This is a test input for integration testing. It should be summarized based on tenant settings.";
        
        SummarizationRequest requestBody = new SummarizationRequest(inputText);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-TENANT-ID", tenantId); // Add tenant ID header
        HttpEntity<SummarizationRequest> request = new HttpEntity<>(requestBody, headers);
        
        String url = "http://localhost:" + port + "/api/v1/enrichment/summarize";
        
        // Act
        ResponseEntity<SummaryResponse> response = 
                restTemplate.postForEntity(url, request, SummaryResponse.class);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(tenantId, response.getBody().getTenantId());
        assertNotNull(response.getBody().getSummary());
    }
    
    @Test
    void summarizeEndpoint_WithUnknownTenant_Returns404NotFound() {
        // Arrange
        String tenantId = "unknown-tenant";
        String inputText = "This is a test input that should fail due to unknown tenant.";
        
        SummarizationRequest requestBody = new SummarizationRequest(inputText);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-TENANT-ID", tenantId);
        HttpEntity<SummarizationRequest> request = new HttpEntity<>(requestBody, headers);
        
        String url = "http://localhost:" + port + "/api/v1/enrichment/summarize";
        
        // Act
        ResponseEntity<Object> response = 
                restTemplate.postForEntity(url, request, Object.class);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    void summarizeEndpoint_WithMissingTenantIdHeader_Returns400BadRequest() {
        // Arrange
        String inputText = "This is a test input that should fail due to missing tenant ID header.";
        
        SummarizationRequest requestBody = new SummarizationRequest(inputText);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // No X-TENANT-ID header
        HttpEntity<SummarizationRequest> request = new HttpEntity<>(requestBody, headers);
        
        String url = "http://localhost:" + port + "/api/v1/enrichment/summarize";
        
        // Act
        ResponseEntity<String> response = 
                restTemplate.postForEntity(url, request, String.class);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void summarizeEndpoint_WithInputExceedingMaxTokens_Returns400BadRequest() {
        // Arrange
        String tenantId = "tenant1"; // Tenant1 has a max token limit of 300 (from InMemoryTenantSettingsRepository)
        
        // Create a string longer than max tokens
        StringBuilder longInput = new StringBuilder();
        for (int i = 0; i < 400; i++) { // Exceeding the 300 limit
            longInput.append("a");
        }
        
        SummarizationRequest requestBody = new SummarizationRequest(longInput.toString());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-TENANT-ID", tenantId);
        HttpEntity<SummarizationRequest> request = new HttpEntity<>(requestBody, headers);
        
        String url = "http://localhost:" + port + "/api/v1/enrichment/summarize";
        
        // Act
        ResponseEntity<Object> response = 
                restTemplate.postForEntity(url, request, Object.class);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
