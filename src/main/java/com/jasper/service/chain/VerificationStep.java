package com.jasper.service.chain;

/**
 * Verification Step Interface (Chain of Responsibility Pattern)
 * Each step in the verification chain implements this interface
 */
public interface VerificationStep {
    
    /**
     * Execute this verification step
     *
     * @param context the verification context containing current state
     * @return true if chain should continue, false if chain should stop
     */
    boolean execute(VerificationContext context);
    
    /**
     * Get step name for logging
     */
    String getStepName();
}
