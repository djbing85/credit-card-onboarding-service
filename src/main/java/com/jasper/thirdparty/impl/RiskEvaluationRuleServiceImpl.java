package com.jasper.thirdparty.impl;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.thirdparty.RiskEvaluationServiceClient;
import com.jasper.thirdparty.RuleVerificationService;
import com.jasper.thirdparty.dto.RiskEvaluationRequestDTO;
import com.jasper.thirdparty.vo.RiskEvaluationResponseVO;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Risk Evaluation Rule Verification Service Implementation
 */
@Slf4j
@Service
public class RiskEvaluationRuleServiceImpl implements RuleVerificationService {

    public static final String SERVICE_NAME = "Risk Evaluation";

    @Autowired
    private RiskEvaluationServiceClient riskEvaluationServiceClient;

    @Override
    public RuleVerificationResponseVO verify(CreditCardOnboarding onboarding) {
        try {
            // Convert to DTO
            RiskEvaluationRequestDTO request = new RiskEvaluationRequestDTO();
            request.setEmiratesIdNumber(onboarding.getEmiratesIdNumber());
            request.setName(onboarding.getName());
            request.setIncome(onboarding.getIncome());
            request.setRequestedCreditLimit(onboarding.getRequestedCreditLimit());

            // Call third-party service
            RiskEvaluationResponseVO response = riskEvaluationServiceClient.evaluateRisk(request);

            // Convert to unified response
            BigDecimal riskScore = response.getRiskScore();
            Boolean verified = true;

            RuleVerificationResponseVO result = new RuleVerificationResponseVO(verified, riskScore, SERVICE_NAME, null);
            log.info("{} completed for Emirates ID: {}, verified: {}, riskScore: {}",
                    SERVICE_NAME, onboarding.getEmiratesIdNumber(), verified, riskScore);
            return result;
        } catch (Exception e) {
            log.error("Error in risk evaluation for Emirates ID: {}", onboarding.getEmiratesIdNumber(), e);
            return new RuleVerificationResponseVO(false, BigDecimal.ZERO, SERVICE_NAME, e.getMessage());
        }
    }
}
