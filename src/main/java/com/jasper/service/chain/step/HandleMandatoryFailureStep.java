package com.jasper.service.chain.step;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.service.CreditCardOnboardingService;
import com.jasper.service.builder.VerificationDetailBuilder;
import com.jasper.service.calculator.ScoreCalculator;
import com.jasper.service.chain.VerificationContext;
import com.jasper.service.chain.VerificationStep;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

/**
 * Step 5: Handle Mandatory Failure
 * Handles the case when mandatory rules fail - updates DB with rejection
 */
@Slf4j
@Component
public class HandleMandatoryFailureStep implements VerificationStep {
    
    @Autowired
    private CreditCardOnboardingService onboardingService;
    
    @Autowired
    private VerificationDetailBuilder detailBuilder;
    @Autowired
    private ScoreCalculator scoreCalculator;
    
    @Override
    public boolean execute(VerificationContext context) {
        // Check if we need to handle mandatory failure
        // Only execute if mandatory failed AND no result has been calculated yet
        if (!"MANDATORY_FAILED".equals(context.getVerifiedResult()) || context.getTotalScore() != null) {
            log.debug("Step 5 skipped: No mandatory failure to handle or already processed");
            return true; // Continue chain - nothing to do
        }
        
        log.debug("Step 5: Handling mandatory rule failure");
        
        // Determine verified result based on error presence
        String verifiedResult = hasAnyError(context.getMandatoryResults()) ? 
                CreditCardOnboarding.VERIFIED_RESULT_ERROR : 
                CreditCardOnboarding.VERIFIED_RESULT_REJECTED;
        
        // Build verification detail JSON
        String verifiedDetail = detailBuilder.buildVerificationDetailJson(
            context.getMandatoryResults(), 
            null
        );
        
        // Calculate score (even error / rejected)
        BigDecimal totalScore = scoreCalculator.calculateTotalScore(
                context.getMandatoryResults(),
                context.getNonMandatoryResults()
        );
        // Update database with rejection
        onboardingService.updateAllVerificationFields(
            context.getOnboardingId(), 
            verifiedResult, 
            totalScore.toString(), 
            verifiedDetail
        );
        
        // Get updated onboarding
        CreditCardOnboarding updatedOnboarding = onboardingService.getById(context.getOnboardingId());
        context.setUpdatedOnboarding(updatedOnboarding);
        context.setVerifiedResult(verifiedResult);
        context.setTotalScore(totalScore);
        
        log.warn("Step 5 completed: Application rejected due to mandatory rule failure. Result={}", verifiedResult);
        return false; // Stop chain - no need to continue
    }
    
    /**
     * Check if any rule has error message
     */
    private boolean hasAnyError(java.util.Map<String, RuleVerificationResponseVO> results) {
        if (results == null || results.isEmpty()) {
            return false;
        }
        return results.values().stream()
                .anyMatch(result -> result.getErrorMessage() != null && !result.getErrorMessage().isEmpty());
    }
    
    @Override
    public String getStepName() {
        return "HandleMandatoryFailure";
    }
}
