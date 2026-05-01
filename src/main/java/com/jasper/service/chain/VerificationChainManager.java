package com.jasper.service.chain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Verification Chain Manager
 * Manages and executes the verification chain of responsibility
 */
@Slf4j
@Component
public class VerificationChainManager {
    
    @Autowired
    private List<VerificationStep> verificationSteps;
    
    /**
     * Execute the verification chain
     *
     * @param context the verification context
     * @return true if chain completed successfully, false if stopped early
     */
    public boolean executeChain(VerificationContext context) {
        log.debug("Starting verification chain with {} steps", verificationSteps.size());
        
        for (VerificationStep step : verificationSteps) {
            log.debug("Executing step: {}", step.getStepName());
            
            try {
                boolean shouldContinue = step.execute(context);
                
                if (!shouldContinue) {
                    log.debug("Chain stopped at step: {}", step.getStepName());
                    return false;
                }
            } catch (Exception e) {
                log.error("Error executing step: {}", step.getStepName(), e);
                return false;
            }
        }
        
        log.debug("Verification chain completed successfully");
        return true;
    }
}
