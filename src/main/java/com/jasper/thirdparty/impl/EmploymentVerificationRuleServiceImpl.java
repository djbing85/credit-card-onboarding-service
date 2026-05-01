package com.jasper.thirdparty.impl;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.thirdparty.EmploymentVerificationServiceClient;
import com.jasper.thirdparty.RuleVerificationService;
import com.jasper.thirdparty.dto.EmploymentVerificationRequestDTO;
import com.jasper.thirdparty.vo.EmploymentVerificationResponseVO;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Employment Verification Rule Verification Service Implementation
 */
@Slf4j
@Service
public class EmploymentVerificationRuleServiceImpl implements RuleVerificationService {

    public static final String SERVICE_NAME = "Employment Verification";

    @Autowired
    private EmploymentVerificationServiceClient employmentVerificationServiceClient;

    @Override
    public RuleVerificationResponseVO verify(CreditCardOnboarding onboarding) {
        try {
            // Convert to DTO
            EmploymentVerificationRequestDTO request = new EmploymentVerificationRequestDTO();
            request.setEmiratesIdNumber(onboarding.getEmiratesIdNumber());
            request.setName(onboarding.getName());
            request.setEmploymentDetails(onboarding.getEmploymentDetails());
            request.setIncome(onboarding.getIncome());

            // Call third-party service
            EmploymentVerificationResponseVO response = employmentVerificationServiceClient.verifyEmployment(request);

            // Convert to unified response
            Boolean verified = response.getVerified();
            BigDecimal riskScore = verified != null && verified ? BigDecimal.ONE : BigDecimal.ZERO;

            RuleVerificationResponseVO result = new RuleVerificationResponseVO(verified, riskScore, SERVICE_NAME, null);
            log.info("{} completed for Emirates ID: {}, verified: {}, riskScore: {}",
                    SERVICE_NAME, onboarding.getEmiratesIdNumber(), verified, riskScore);
            return result;
        } catch (Exception e) {
            log.error("Error in employment verification for Emirates ID: {}", onboarding.getEmiratesIdNumber(), e);
            return new RuleVerificationResponseVO(false, BigDecimal.ZERO, SERVICE_NAME, e.getMessage());
        }
    }
}
