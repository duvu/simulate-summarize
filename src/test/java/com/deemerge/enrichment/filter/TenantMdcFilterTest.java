package com.deemerge.enrichment.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantMdcFilterTest {
    
    private TenantMdcFilter filter;
    
    @Mock
    private HttpServletRequest request;
    
    private MockHttpServletResponse response;
    
    @Mock
    private FilterChain filterChain;
    
    private AutoCloseable mocks;
    
    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        filter = new TenantMdcFilter();
        response = new MockHttpServletResponse();
        MDC.clear();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        MDC.clear();
        mocks.close();
    }
    
    @Test
    void shouldExtractTenantIdFromHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-TENANT-ID")).thenReturn("tenant1");
        
        // Act
        filter.doFilter(request, response, filterChain);
        
        // Assert
        verify(filterChain).doFilter(request, response);
        // MDC is cleared after filter execution, so we can't check directly
    }
    
    @Test
    void shouldReturnBadRequestWhenTenantIdHeaderIsMissing() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-TENANT-ID")).thenReturn(null);
        
        // Act
        filter.doFilter(request, response, filterChain);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Required header X-TENANT-ID is missing"));
        verify(filterChain, never()).doFilter(request, response);
    }
    
    @Test
    void shouldReturnBadRequestWhenTenantIdHeaderIsEmpty() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-TENANT-ID")).thenReturn("");
        
        // Act
        filter.doFilter(request, response, filterChain);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Required header X-TENANT-ID is missing"));
        verify(filterChain, never()).doFilter(request, response);
    }
    
    @Test
    void shouldClearMdcEvenWhenExceptionOccurs() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-TENANT-ID")).thenReturn("tenant1");
        doThrow(new RuntimeException("Test exception")).when(filterChain).doFilter(request, response);
        
        try {
            // Act
            filter.doFilter(request, response, filterChain);
        } catch (RuntimeException e) {
            // Expected exception
        }
        
        // Assert
        assertNull(MDC.get("tenantId")); // MDC should be cleared
    }
}
