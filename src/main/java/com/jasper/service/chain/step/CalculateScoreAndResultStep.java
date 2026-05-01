package com.jasper.service.chain.step;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.service.CreditCardOnboardingService;
import com.jasper.service.builder.VerificationDetailBuilder;
import com.jasper.service.calculator.ScoreCalculator;
import com.jasper.service.chain.VerificationContext;
import com.jasper.service.chain.VerificationStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Step 4: Calculate Score and Determine Result
 * Calculates total score and determines verification result based on thresholds
 */
@Slf4j
@Component
public class CalculateScoreAndResultStep implements VerificationStep {
    
    @Autowired
    private ScoreCalculator scoreCalculator;
    
    @Autowired
    private VerificationDetailBuilder detailBuilder;
    
    @Autowired
    private CreditCardOnboardingService onboardingService;
    
    @Value("${app.verification.threshold-auto-issue:0.9}")
    private BigDecimal thresholdAutoIssue;
    
    @Value("${app.verification.threshold-manual-review-limit:0.75}")
    private BigDecimal thresholdManualReviewLimit;
    
    @Value("${app.verification.threshold-manual-review:0.5}")
    private BigDecimal thresholdManualReview;
    
    @Override
    public boolean execute(VerificationContext context) {
        log.debug("Step 4: Calculating score and determining result");
        
        // Always calculate total score (even if there are errors)
        BigDecimal totalScore = scoreCalculator.calculateTotalScore(
            context.getMandatoryResults(), 
            context.getNonMandatoryResults()
        );
        context.setTotalScore(totalScore);
        
        // Check if any rule (mandatory or non-mandatory) has error message
        String verifiedResult = checkForErrorsAndDetermineResult(context);
        
        if (CreditCardOnboarding.VERIFIED_RESULT_ERROR.equals(verifiedResult)) {
            // Has error - use ERROR result but keep calculated score
            context.setVerifiedResult(verifiedResult);
        } else {
            // No error - determine verification result based on score thresholds
            verifiedResult = determineVerifiedResultByScore(totalScore);
            context.setVerifiedResult(verifiedResult);
        }
        
        // Build verification detail JSON
        String verifiedDetail = detailBuilder.buildVerificationDetailJson(
            context.getMandatoryResults(), 
            context.getNonMandatoryResults()
        );
        context.setVerifiedDetail(verifiedDetail);
        
        // Update database with all verification fields
        onboardingService.updateAllVerificationFields(
            context.getOnboardingId(), 
            verifiedResult, 
            context.getTotalScore().toString(), 
            verifiedDetail
        );
        
        // Get updated onboarding
        CreditCardOnboarding updatedOnboarding = onboardingService.getById(context.getOnboardingId());
        context.setUpdatedOnboarding(updatedOnboarding);
        
        log.debug("Step 4 completed: Score={}, Result={}", context.getTotalScore(), verifiedResult);
        return true; // Continue chain (though this is the last step)
    }

    /**
     * Check for errors in rule results and determine if ERROR result should be returned
     */
    private String checkForErrorsAndDetermineResult(VerificationContext context) {
        // Check mandatory results for errors
        if (hasAnyError(context.getMandatoryResults())) {
            return CreditCardOnboarding.VERIFIED_RESULT_ERROR;
        }
        
        // Check non-mandatory results for errors
        if (hasAnyError(context.getNonMandatoryResults())) {
            return CreditCardOnboarding.VERIFIED_RESULT_ERROR;
        }
        
        return null; // No errors, continue with score-based decision
    }
    
    /**
     * Check if any rule has error message
     */
    private boolean hasAnyError(java.util.Map<String, com.jasper.thirdparty.vo.RuleVerificationResponseVO> results) {
        if (results == null || results.isEmpty()) {
            return false;
        }
        return results.values().stream()
                .anyMatch(result -> result.getErrorMessage() != null && !result.getErrorMessage().isEmpty());
    }

    /**
     * Determine verification result based on score thresholds
     */
    private String determineVerifiedResultByScore(BigDecimal totalScore) {
        if (totalScore.compareTo(thresholdAutoIssue) >= 0) {
            // Card is issued automatically with requested credit (score >= 0.9)
            return CreditCardOnboarding.VERIFIED_RESULT_AUTO_ISSUE;
        } else if (totalScore.compareTo(thresholdManualReviewLimit) >= 0) {
            // Card is issued automatically with credit limit set manually after review (score >= 0.75)
            return CreditCardOnboarding.VERIFIED_RESULT_MANUAL_REVIEW_LIMIT;
        } else if (totalScore.compareTo(thresholdManualReview) >= 0) {
            // Manual Review (score >= 0.5)
            return CreditCardOnboarding.VERIFIED_RESULT_MANUAL_REVIEW;
        } else {
            // Score below manual review threshold - rejected
            return CreditCardOnboarding.VERIFIED_RESULT_REJECTED;
        }
    }
    
    @Override
    public String getStepName() {
        return "CalculateScoreAndResult";
    }
}
