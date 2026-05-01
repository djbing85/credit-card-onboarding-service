package com.jasper.thirdparty.impl;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.thirdparty.ComplianceCheckServiceClient;
import com.jasper.thirdparty.RuleVerificationService;
import com.jasper.thirdparty.dto.ComplianceCheckRequestDTO;
import com.jasper.thirdparty.vo.ComplianceCheckResponseVO;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Compliance Check Rule Verification Service Implementation
 */
@Slf4j
@Service
public class ComplianceCheckRuleServiceImpl implements RuleVerificationService {

    public static final String SERVICE_NAME = "Compliance Check";

    @Autowired
    private ComplianceCheckServiceClient complianceCheckServiceClient;

    @Override
    public RuleVerificationResponseVO verify(CreditCardOnboarding onboarding) {
        try {
            // Convert to DTO
            ComplianceCheckRequestDTO request = new ComplianceCheckRequestDTO();
            request.setEmiratesIdNumber(onboarding.getEmiratesIdNumber());
            request.setName(onboarding.getName());
            request.setNationality(onboarding.getNationality());
            request.setAddress(onboarding.getAddress());

            // Call third-party service
            ComplianceCheckResponseVO response = complianceCheckServiceClient.checkCompliance(request);

            // Convert to unified response
            Boolean verified = response.getPassed();
            BigDecimal riskScore = verified != null && verified ? BigDecimal.ONE : BigDecimal.ZERO;

            RuleVerificationResponseVO result = new RuleVerificationResponseVO(verified, riskScore, SERVICE_NAME, null);
            log.info("{} completed for Emirates ID: {}, verified: {}, riskScore: {}",
                    SERVICE_NAME, onboarding.getEmiratesIdNumber(), verified, riskScore);
            return result;
        } catch (Exception e) {
            log.error("Error in compliance check for Emirates ID: {}", onboarding.getEmiratesIdNumber(), e);
            return new RuleVerificationResponseVO(false, BigDecimal.ZERO, SERVICE_NAME, e.getMessage());
        }
    }
}
