package com.jasper.thirdparty.impl;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.thirdparty.BehavioralAnalysisServiceClient;
import com.jasper.thirdparty.RuleVerificationService;
import com.jasper.thirdparty.dto.BehavioralAnalysisRequestDTO;
import com.jasper.thirdparty.vo.BehavioralAnalysisResponseVO;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Behavioral Analysis Rule Verification Service Implementation
 */
@Slf4j
@Service
public class BehavioralAnalysisRuleServiceImpl implements RuleVerificationService {

    public static final String SERVICE_NAME = "Behavioral Analysis";

    @Autowired
    private BehavioralAnalysisServiceClient behavioralAnalysisServiceClient;

    @Override
    public RuleVerificationResponseVO verify(CreditCardOnboarding onboarding) {
        try {
            // Convert to DTO
            BehavioralAnalysisRequestDTO request = new BehavioralAnalysisRequestDTO();
            request.setEmiratesIdNumber(onboarding.getEmiratesIdNumber());
            request.setName(onboarding.getName());
            request.setBankStatement(onboarding.getBankStatement());
            request.setIncome(onboarding.getIncome());

            // Call third-party service
            BehavioralAnalysisResponseVO response = behavioralAnalysisServiceClient.analyzeBehavior(request);

            // Convert to unified response
            BigDecimal riskScore = response.getBehavioralScore();
            Boolean verified = true;

            RuleVerificationResponseVO result = new RuleVerificationResponseVO(verified, riskScore, SERVICE_NAME, null);
            log.info("{} completed for Emirates ID: {}, verified: {}, riskScore: {}",
                    SERVICE_NAME, onboarding.getEmiratesIdNumber(), verified, riskScore);
            return result;
        } catch (Exception e) {
            log.error("Error in behavioral analysis for Emirates ID: {}", onboarding.getEmiratesIdNumber(), e);
            return new RuleVerificationResponseVO(false, BigDecimal.ZERO, SERVICE_NAME, e.getMessage());
        }
    }
}
