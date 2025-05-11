package com.deemerge.enrichment.exception;

/**
 * Exception thrown when a tenant cannot be found by its ID
 */
public class TenantNotFoundException extends RuntimeException {

    public TenantNotFoundException(String tenantId) {
        super("Unknown tenant: " + tenantId);
    }
}
