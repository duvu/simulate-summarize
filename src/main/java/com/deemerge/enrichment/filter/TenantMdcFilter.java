package com.deemerge.enrichment.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that extracts tenant ID from X-TENANT-ID header and sets it in MDC
 * for consistent logging across the application.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TenantMdcFilter implements Filter {

    private static final String TENANT_ID_KEY = "tenantId";
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String TENANT_ID_HEADER = "X-TENANT-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        try {
            // Generate unique request ID
            String requestId = UUID.randomUUID().toString();
            MDC.put(REQUEST_ID_KEY, requestId);
            
            // Extract and set tenant ID from header
            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                String tenantId = httpRequest.getHeader(TENANT_ID_HEADER);
                
                if (!StringUtils.hasText(tenantId)) {
                    // Tenant ID header is missing or empty
                    log.error("Required header {} is missing", TENANT_ID_HEADER);
                    httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    httpResponse.getWriter().write("Required header X-TENANT-ID is missing");
                    return; // Stop filter chain
                }
                
                // Set tenant ID in MDC
                MDC.put(TENANT_ID_KEY, tenantId);
                log.debug("Set tenant ID in MDC from header: {}", tenantId);
                
                log.debug("Processing request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
            }
            
            // Continue the filter chain
            chain.doFilter(request, response);
            
        } finally {
            MDC.clear();
            log.debug("Cleared MDC context");
        }
    }
}
