package com.deemerge.enrichment.controller;

import com.deemerge.enrichment.exception.EmptyInputException;
import com.deemerge.enrichment.model.SummarizationRequest;
import com.deemerge.enrichment.model.SummaryResponse;
import com.deemerge.enrichment.service.EnrichmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for enrichment operations
 */
@RestController
@RequestMapping("/api/v1/enrichment")
@RequiredArgsConstructor
public class EnrichmentController {

    private final EnrichmentService enrichmentService;
    
    /**
     * Endpoint to summarize text for a tenant specified in X-TENANT-ID header
     * Request body should be JSON with input_text field
     */
    @PostMapping("/summarize")
    public ResponseEntity<SummaryResponse> summarize(
            @RequestHeader("X-TENANT-ID") String tenantId,
            @RequestBody SummarizationRequest request) {
        
        if (request.getInput_text() == null) {
            throw new EmptyInputException();
        }
        
        SummaryResponse response = enrichmentService.summarize(tenantId, request.getInput_text());
        return ResponseEntity.ok(response);
    }
}
