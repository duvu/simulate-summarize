package com.deemerge.enrichment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for summarization
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummarizationRequest {
    
    private String input_text;
    
}
