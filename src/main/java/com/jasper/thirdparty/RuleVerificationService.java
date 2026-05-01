package com.jasper.thirdparty;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;

/**
 * Rule Verification Service Interface
 * Unified interface for all rule verification services
 */
public interface RuleVerificationService {
    
    /**
     * Verify the rule based on onboarding application data
     *
     * @param onboarding credit card onboarding application data
     * @return unified verification response with verified status and risk score
     */
    RuleVerificationResponseVO verify(CreditCardOnboarding onboarding);
}
