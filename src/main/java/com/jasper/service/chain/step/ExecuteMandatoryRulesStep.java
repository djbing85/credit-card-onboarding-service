package com.jasper.service.chain.step;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jasper.common.CustomException;
import com.jasper.entity.CreditCardOnboarding;
import com.jasper.service.CreditCardOnboardingService;
import com.jasper.service.builder.VerificationDetailBuilder;
import com.jasper.service.calculator.ScoreCalculator;
import com.jasper.service.chain.VerificationContext;
import com.jasper.service.chain.VerificationStep;
import com.jasper.service.executor.RuleExecutor;
import com.jasper.thirdparty.RuleVerificationService;
import com.jasper.thirdparty.factory.CreditCardOnboardingRuleFactory;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Step 2: Execute Mandatory Rules
 * Executes all mandatory rule verification services concurrently
 * If any mandatory rule fails, stops the chain and sets score to 0
 */
@Slf4j
@Component
public class ExecuteMandatoryRulesStep implements VerificationStep {
    
    @Autowired
    private CreditCardOnboardingRuleFactory ruleFactory;
    
    @Autowired
    private RuleExecutor ruleExecutor;
    
    @Autowired
    private CreditCardOnboardingService onboardingService;
    
    @Autowired
    private VerificationDetailBuilder detailBuilder;

    @Autowired
    private ScoreCalculator scoreCalculator;
    
    @Override
    public boolean execute(VerificationContext context) {
        log.debug("Executing mandatory rules");
        
        // Get mandatory rule services
        Map<String, RuleVerificationService> mandatoryRuleMap = ruleFactory.getAllRuleServices(true);
        
        // Check if both mandatory and non-mandatory rules are empty
        Map<String, RuleVerificationService> nonMandatoryRuleMap = ruleFactory.getAllRuleServices(false);
        
        if (CollectionUtils.isEmpty(mandatoryRuleMap) && CollectionUtils.isEmpty(nonMandatoryRuleMap)) {
            throw CustomException.internalError("No rules configured - both mandatory and non-mandatory rule sets are empty");
        }
        
        // Execute mandatory rules
        Map<String, RuleVerificationResponseVO> mandatoryResults = 
            ruleExecutor.executeRules(context.getOnboarding(), mandatoryRuleMap, "mandatory");
        
        context.setMandatoryResults(mandatoryResults);
        
        // Check if any mandatory rule failed
        if (!CollectionUtils.isEmpty(mandatoryRuleMap) && hasMandatoryFailure(mandatoryResults)) {
            log.warn("Mandatory rule failure detected - stopping chain, score will be 0");
            
            // Handle mandatory failure immediately
            handleMandatoryFailure(context, mandatoryResults);
            
            return false; // Stop chain - no need to execute non-mandatory rules or calculate score
        }
        
        log.debug("completed: All mandatory rules passed or no mandatory rules");
        return true; // Continue chain
    }
    
    /**
     * Handle mandatory rule failure - set result to REJECTED/ERROR with score 0
     */
    private void handleMandatoryFailure(VerificationContext context, 
                                       Map<String, RuleVerificationResponseVO> mandatoryResults) {
        // Determine verified result based on error presence
        String verifiedResult = hasAnyError(mandatoryResults) ? 
                CreditCardOnboarding.VERIFIED_RESULT_ERROR : 
                CreditCardOnboarding.VERIFIED_RESULT_REJECTED;
        
        // Build verification detail JSON
        String verifiedDetail = detailBuilder.buildVerificationDetailJson(mandatoryResults, null);
        
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
        context.setVerifiedDetail(verifiedDetail);
        
        log.warn("Mandatory failure handled: Result={}, Score={}", verifiedResult, totalScore);
    }
    
    private boolean hasMandatoryFailure(Map<String, RuleVerificationResponseVO> mandatoryResults) {
        if (mandatoryResults == null || mandatoryResults.isEmpty()) {
            return false;
        }
        return mandatoryResults.values().stream()
                .anyMatch(result -> !Boolean.TRUE.equals(result.getVerified()));
    }
    
    /**
     * Check if any rule has error message
     */
    private boolean hasAnyError(Map<String, RuleVerificationResponseVO> results) {
        if (results == null || results.isEmpty()) {
            return false;
        }
        return results.values().stream()
                .anyMatch(result -> result.getErrorMessage() != null && !result.getErrorMessage().isEmpty());
    }
    
    @Override
    public String getStepName() {
        return "ExecuteMandatoryRules";
    }
}
