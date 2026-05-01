package com.jasper.service;

import com.jasper.common.CustomException;
import com.jasper.common.SpringContextHolder;
import com.jasper.entity.CreditCardOnboarding;
import com.jasper.service.chain.VerificationChainManager;
import com.jasper.service.chain.VerificationContext;
import com.jasper.service.event.VerificationCompletedEvent;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Credit Card Onboarding Verification Service
 * Handles all verification logic for credit card onboarding applications
 * 
 * Refactored to use:
 * - Chain of Responsibility Pattern for verification flow
 * - Strategy Pattern for score calculation
 * - Observer Pattern for event notification
 * - Single Responsibility Principle with delegated components
 */
@Slf4j
@Service
public class CreditCardOnboardingVerifyService {

    @Autowired
    private VerificationChainManager chainManager;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Verify onboarding application through all rule services
     * Uses Chain of Responsibility pattern for verification flow
     *
     * @param id onboarding application ID
     * @return verification result with score and details
     */
    public VerificationResult verify(Long id) {
        try {
            log.info("Starting verification for onboarding ID: {}", id);
            
            // Create verification context
            VerificationContext context = new VerificationContext(id);
            
            // Execute verification chain
            // Note: CustomException will be thrown and not caught here
            boolean success = chainManager.executeChain(context);
            
            if (context.getUpdatedOnboarding() == null) {
                // Error occurred during verification
                return VerificationResult.error(
                    context.getOnboarding(), 
                    combineResults(context.getMandatoryResults(), context.getNonMandatoryResults())
                );
            }
            
            // Combine all results
            Map<String, RuleVerificationResponseVO> allResults = combineResults(
                context.getMandatoryResults(), 
                context.getNonMandatoryResults()
            );
            
            // Publish verification completed event
            publishVerificationEvent(context);
            
            log.info("Verification completed for ID: {}, Result: {}", id, context.getVerifiedResult());
            
            return VerificationResult.of(context.getUpdatedOnboarding(), allResults);

        } catch (CustomException e) {
            // Re-throw CustomException (e.g., not found)
            throw e;
        } catch (RuntimeException e) {
            log.error("Rule configuration error during verification for ID: {}", id, e);
            return handleVerificationError(id, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during verification for ID: {}", id, e);
            return handleVerificationError(id, "Unexpected error: " + e.getMessage());
        }
    }
    
    /**
     * Combine mandatory and non-mandatory results
     */
    private Map<String, RuleVerificationResponseVO> combineResults(
            Map<String, RuleVerificationResponseVO> mandatoryResults,
            Map<String, RuleVerificationResponseVO> nonMandatoryResults) {
        Map<String, RuleVerificationResponseVO> allResults = new HashMap<>();
        if (mandatoryResults != null) {
            allResults.putAll(mandatoryResults);
        }
        if (nonMandatoryResults != null) {
            allResults.putAll(nonMandatoryResults);
        }
        return allResults;
    }
    
    /**
     * Handle verification error
     */
    private VerificationResult handleVerificationError(Long id, String errorMessage) {
        CreditCardOnboarding onboarding = null;
        try {
            // Try to get onboarding data
            var onboardingService = SpringContextHolder.getApplicationContext()
                .getBean(CreditCardOnboardingService.class);
            onboarding = onboardingService.getById(id);
        } catch (Exception e) {
            log.error("Failed to retrieve onboarding for error handling", e);
        }
        
        Map<String, RuleVerificationResponseVO> errorResult = new HashMap<>();
        errorResult.put("SYSTEM", new RuleVerificationResponseVO(
            false, 
            java.math.BigDecimal.ZERO, 
            "SYSTEM", 
            errorMessage
        ));
        
        return VerificationResult.error(onboarding, errorResult);
    }
    
    /**
     * Publish verification completed event
     */
    private void publishVerificationEvent(VerificationContext context) {
        try {
            if (context.getUpdatedOnboarding() != null && context.getVerifiedResult() != null) {
                VerificationCompletedEvent event = new VerificationCompletedEvent(
                    this,
                    context.getUpdatedOnboarding(),
                    context.getVerifiedResult(),
                    context.getTotalScore() != null ? context.getTotalScore().toString() : null,
                    context.getVerifiedDetail()
                );
                eventPublisher.publishEvent(event);
            }
        } catch (Exception e) {
            log.error("Failed to publish verification event", e);
        }
    }

    /**
     * Verification result wrapper
     */
    public static class VerificationResult {
        private final CreditCardOnboarding onboarding;
        private final Map<String, RuleVerificationResponseVO> ruleResults;
        private final boolean configError;

        private VerificationResult(CreditCardOnboarding onboarding, Map<String, RuleVerificationResponseVO> ruleResults, boolean configError) {
            this.onboarding = onboarding;
            this.ruleResults = ruleResults;
            this.configError = configError;
        }

        public static VerificationResult of(CreditCardOnboarding onboarding, Map<String, RuleVerificationResponseVO> ruleResults) {
            return new VerificationResult(onboarding, ruleResults, false);
        }

        public static VerificationResult notFound() {
            return new VerificationResult(null, new HashMap<>(), false);
        }

        public static VerificationResult error(CreditCardOnboarding onboarding, Map<String, RuleVerificationResponseVO> ruleResults) {
            return new VerificationResult(onboarding, ruleResults, true);
        }

        public boolean isNotFound() {
            return onboarding == null;
        }

        public boolean isConfigError() {
            return configError;
        }

        public String getVerifiedResult() {
            if (isNotFound()) {
                return null;
            }
            if (configError) {
                return CreditCardOnboarding.VERIFIED_RESULT_ERROR;
            }
            return onboarding.getVerifiedResult();
        }

        public CreditCardOnboarding getOnboarding() {
            return onboarding;
        }

        public Map<String, RuleVerificationResponseVO> getRuleResults() {
            return ruleResults;
        }
    }
}
