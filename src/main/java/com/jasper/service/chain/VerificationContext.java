package com.jasper.service.chain;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Verification Context
 * Holds the state throughout the verification chain
 */
@Data
public class VerificationContext {
    
    private final Long onboardingId;
    private CreditCardOnboarding onboarding;
    
    // Rule execution results
    private Map<String, RuleVerificationResponseVO> mandatoryResults = new HashMap<>();
    private Map<String, RuleVerificationResponseVO> nonMandatoryResults = new HashMap<>();
    
    // Calculation results
    private BigDecimal totalScore;
    private String verifiedResult;
    private String verifiedDetail;
    
    // Final result
    private CreditCardOnboarding updatedOnboarding;
    
    public VerificationContext(Long onboardingId) {
        this.onboardingId = onboardingId;
    }
}
