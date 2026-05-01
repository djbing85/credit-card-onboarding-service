package com.jasper.service.strategy;

import com.jasper.entity.CreditCardOnboardingRules;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;

import java.math.BigDecimal;

/**
 * Score Calculation Strategy Interface
 * Defines how to calculate score contribution from a rule verification result
 */
public interface ScoreCalculationStrategy {
    
    /**
     * Calculate score contribution based on rule configuration and verification result
     *
     * @param rule the rule configuration containing score, scoreContribution, scoreType
     * @param result the verification result from third-party service
     * @return calculated score contribution
     */
    BigDecimal calculate(CreditCardOnboardingRules rule, RuleVerificationResponseVO result);
    
    /**
     * Get the score type this strategy handles
     *
     * @return score type constant (0 for BOOLEAN, 1 for DECIMAL)
     */
    int getScoreType();
}
