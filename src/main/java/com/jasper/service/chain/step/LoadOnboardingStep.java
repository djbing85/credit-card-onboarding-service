package com.jasper.service.chain.step;

import com.jasper.common.CustomException;
import com.jasper.entity.CreditCardOnboarding;
import com.jasper.service.CreditCardOnboardingService;
import com.jasper.service.chain.VerificationContext;
import com.jasper.service.chain.VerificationStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Step 1: Load Onboarding Data
 * Loads the credit card onboarding application from database
 */
@Slf4j
@Component
public class LoadOnboardingStep implements VerificationStep {
    
    @Autowired
    private CreditCardOnboardingService onboardingService;
    
    @Override
    public boolean execute(VerificationContext context) {
        log.debug("Loading onboarding data for ID: {}", context.getOnboardingId());
        
        CreditCardOnboarding onboarding = onboardingService.getById(context.getOnboardingId());
        
        if (onboarding == null) {
            throw CustomException.notFound("Onboarding not found for ID: " + context.getOnboardingId());
        }
        
        context.setOnboarding(onboarding);
        log.debug("completed: Onboarding loaded successfully");
        return true; // Continue chain
    }
    
    @Override
    public String getStepName() {
        return "LoadOnboarding";
    }
}
