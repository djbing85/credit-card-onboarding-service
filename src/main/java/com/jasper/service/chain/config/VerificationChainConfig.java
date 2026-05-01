package com.jasper.service.chain.config;

import com.jasper.service.chain.VerificationStep;
import com.jasper.service.chain.step.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Verification Chain Configuration
 * Defines the order of verification steps
 */
@Configuration
public class VerificationChainConfig {
    
    /**
     * Define the ordered list of verification steps
     * The order is critical for proper verification flow
     */
    @Bean
    public List<VerificationStep> verificationSteps(
            LoadOnboardingStep loadOnboardingStep,
            ExecuteMandatoryRulesStep executeMandatoryRulesStep,
            ExecuteNonMandatoryRulesStep executeNonMandatoryRulesStep,
            CalculateScoreAndResultStep calculateScoreAndResultStep) {
        
        List<VerificationStep> steps = new ArrayList<>();
        
        // Step 1: Load onboarding data from DB
        steps.add(loadOnboardingStep);
        
        // Step 2: Execute mandatory rules (handles failure internally)
        steps.add(executeMandatoryRulesStep);
        
        // Step 3: Execute non-mandatory rules (only if mandatory rules pass)
        steps.add(executeNonMandatoryRulesStep);
        
        // Step 4: Calculate score and determine result
        steps.add(calculateScoreAndResultStep);
        
        return steps;
    }
}
