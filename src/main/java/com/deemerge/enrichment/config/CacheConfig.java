package com.deemerge.enrichment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for caching functionality 
 */
@Configuration
public class CacheConfig {
    
    @Autowired
    private TenantAwareCacheManager tenantAwareCacheManager;
    
    @Bean
    @Primary
    public CacheManager cacheManager() {
        // Use our custom tenant-aware cache manager that limits to 5 entries per tenant
        return tenantAwareCacheManager;
    }
}
