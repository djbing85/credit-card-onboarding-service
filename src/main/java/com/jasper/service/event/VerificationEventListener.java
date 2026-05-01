package com.jasper.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Verification Event Listener
 * Listens to verification events and logs them (currently only logging)
 */
@Slf4j
@Component
public class VerificationEventListener {
    
    /**
     * Handle verification completed event
     * Currently only logs the event, but can be extended for:
     * - Sending notifications
     * - Triggering downstream processes
     * - Audit logging
     * - Metrics collection
     */
    @EventListener
    @Async("mdcVirtualThreadExecutor")
    public void handleVerificationCompleted(VerificationCompletedEvent event) {
        log.info("Verification completed - ID: {}, Result: {}, Score: {}", 
                event.getOnboarding().getId(),
                event.getVerifiedResult(),
                event.getTotalScore());
        
        // Future extensions:
        // - Send notification email/SMS
        // - Trigger risk assessment workflows
        // - Update analytics dashboard
        // - Archive verification details
    }
}
