package com.deemerge.enrichment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSettings {
    private String model;        // e.g., "gpt-3.5", "gpt-4", etc.
    private String tone;         // e.g., "formal", "friendly", etc.
    private int maxTokens;       // e.g., 200, 300, etc.
    private int retryAttempts;   // Number of retry attempts for failed API calls
}
