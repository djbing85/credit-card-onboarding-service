package com.jasper.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Virtual Thread Executor Configuration with MDC support
 */
@Configuration
public class VirtualThreadExecutorConfig {

    /**
     * Create a virtual thread executor that supports MDC context propagation
     */
    @Bean(name = "mdcVirtualThreadExecutor")
    public Executor mdcVirtualThreadExecutor() {
        // Create a ThreadFactory that sets virtual thread names
        java.util.concurrent.ThreadFactory threadFactory = Thread.ofVirtual()
                .name("vthread-", 0)
                .factory();
        
        ExecutorService virtualThreadExecutor = Executors.newThreadPerTaskExecutor(threadFactory);
        
        return runnable -> {
            // Capture current MDC context
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            
            virtualThreadExecutor.execute(() -> {
                try {
                    // Restore MDC context in the new thread
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    // Execute the task
                    runnable.run();
                } finally {
                    // Clean up MDC context
                    MDC.clear();
                }
            });
        };
    }
}
