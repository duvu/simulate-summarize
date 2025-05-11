package com.deemerge.enrichment.repository;

import com.deemerge.enrichment.model.TenantSettings;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of TenantSettingsRepository
 * This implementation could be replaced with a database-backed implementation in a production environment
 */
@Repository
public class InMemoryTenantSettingsRepository implements TenantSettingsRepository {

    private final Map<String, TenantSettings> tenantSettingsMap;

    public InMemoryTenantSettingsRepository() {
        // Initialize with some mock tenant settings
        this.tenantSettingsMap = new HashMap<>();
        
        // Add tenant 1 - Uses GPT-4 with formal tone
        tenantSettingsMap.put("tenant1", TenantSettings.builder()
                .model("gpt-4")
                .tone("formal")
                .maxTokens(300)
                .retryAttempts(3)
                .build());
        
        // Add tenant 2 - Uses GPT-3.5 with friendly tone
        tenantSettingsMap.put("tenant2", TenantSettings.builder()
                .model("gpt-3.5")
                .tone("friendly")
                .maxTokens(200)
                .retryAttempts(2)
                .build());
        
        // Add tenant 3 - Uses GPT-3.5 with technical tone
        tenantSettingsMap.put("tenant3", TenantSettings.builder()
                .model("gpt-3.5")
                .tone("technical")
                .maxTokens(250)
                .retryAttempts(3)
                .build());
    }

    @Override
    public Optional<TenantSettings> findByTenantId(String tenantId) {
        return Optional.ofNullable(tenantSettingsMap.get(tenantId));
    }
}
