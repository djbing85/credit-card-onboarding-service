package com.jasper.service.strategy;

import com.jasper.entity.CreditCardOnboardingRules;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Boolean Score Calculation Strategy
 * For boolean type rules: add score if verified, otherwise add zero
 */
@Slf4j
@Component
public class BooleanScoreStrategy implements ScoreCalculationStrategy {
    
    @Override
    public BigDecimal calculate(CreditCardOnboardingRules rule, RuleVerificationResponseVO result) {
        // Boolean type: add score only if verified
        if (Boolean.TRUE.equals(result.getVerified())) {
            return rule.getScore().multiply(rule.getScoreContribution());
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public int getScoreType() {
        return CreditCardOnboardingRules.SCORE_TYPE_BOOLEAN;
    }
}
