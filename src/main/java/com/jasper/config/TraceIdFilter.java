package com.jasper.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to add trace_id to MDC for request tracking
 */
@Component
@Order(1)
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_KEY = "trace_id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // Generate or retrieve trace_id
            String traceId = ((HttpServletRequest) request).getHeader("X-Trace-ID");
            if (StringUtils.isEmpty(traceId)) {
                traceId = UUID.randomUUID().toString().replace("-", "");
            }
            
            // Put trace_id into MDC
            MDC.put(TRACE_ID_KEY, traceId);
            
            // Continue the filter chain
            chain.doFilter(request, response);
        } finally {
            // Clean up MDC to prevent memory leaks
            MDC.clear();
        }
    }
}
