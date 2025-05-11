package com.deemerge.enrichment.repository;

import com.deemerge.enrichment.model.TenantSettings;

import java.util.Optional;

public interface TenantSettingsRepository {
    
    /**
     * Retrieves tenant-specific settings by tenant ID
     * 
     * @param tenantId The unique identifier of the tenant
     * @return Optional TenantSettings object if found, empty otherwise
     */
    Optional<TenantSettings> findByTenantId(String tenantId);
}
