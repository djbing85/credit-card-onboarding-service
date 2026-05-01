package com.jasper.service.strategy;

import com.jasper.entity.CreditCardOnboardingRules;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Decimal Score Calculation Strategy
 * For decimal type rules: totalScore += riskScore * score_contribution
 */
@Slf4j
@Component
public class DecimalScoreStrategy implements ScoreCalculationStrategy {
    
    @Override
    public BigDecimal calculate(CreditCardOnboardingRules rule, RuleVerificationResponseVO result) {
        // Decimal type: riskScore * scoreContribution
        BigDecimal riskScore = result.getRiskScore();
        BigDecimal scoreContribution = rule.getScoreContribution();
        
        if (riskScore == null || scoreContribution == null) {
            log.warn("Null riskScore or scoreContribution for rule: {}", rule.getCriteria());
            return BigDecimal.ZERO;
        }
        
        return riskScore.multiply(scoreContribution);
    }
    
    @Override
    public int getScoreType() {
        return CreditCardOnboardingRules.SCORE_TYPE_DECIMAL;
    }
}
