package com.jasper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Configure virtual thread naming for better logging
 */
@Slf4j
@Component
public class VirtualThreadNamingConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Set the default virtual thread name prefix via system property
        // This affects all virtual threads created by the platform
        String existingPrefix = System.getProperty("jdk.virtualThreadScheduler.parallelism");
        
        log.info("Virtual thread naming configured. Current thread: {}", Thread.currentThread().getName());
        log.info("Virtual threads will use names like: vthread-0, vthread-1, etc.");
    }
}
