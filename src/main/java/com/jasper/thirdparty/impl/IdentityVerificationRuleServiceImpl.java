package com.jasper.thirdparty.impl;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.thirdparty.IdentityVerificationServiceClient;
import com.jasper.thirdparty.RuleVerificationService;
import com.jasper.thirdparty.dto.IdentityVerificationRequestDTO;
import com.jasper.thirdparty.vo.IdentityVerificationResponseVO;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Identity Verification Rule Verification Service Implementation
 */
@Slf4j
@Service
public class IdentityVerificationRuleServiceImpl implements RuleVerificationService {

    public static final String SERVICE_NAME = "Identity Verification";

    @Autowired
    private IdentityVerificationServiceClient identityVerificationServiceClient;

    @Override
    public RuleVerificationResponseVO verify(CreditCardOnboarding onboarding) {
        try {
            // Convert to DTO
            IdentityVerificationRequestDTO request = new IdentityVerificationRequestDTO();
            request.setEmiratesIdNumber(onboarding.getEmiratesIdNumber());
            request.setName(onboarding.getName());
            request.setMobileNumber(onboarding.getMobileNumber());
            request.setNationality(onboarding.getNationality());
            request.setAddress(onboarding.getAddress());

            // Call third-party service
            IdentityVerificationResponseVO response = identityVerificationServiceClient.verifyIdentity(request);

            // Convert to unified response
            Boolean verified = response.getVerified();
            BigDecimal riskScore = verified != null && verified ? BigDecimal.ONE : BigDecimal.ZERO;

            RuleVerificationResponseVO result = new RuleVerificationResponseVO(verified, riskScore, SERVICE_NAME, null);
            log.info("{} completed for Emirates ID: {}, verified: {}, riskScore: {}",
                    SERVICE_NAME, onboarding.getEmiratesIdNumber(), verified, riskScore);
            return result;
        } catch (Exception e) {
            log.error("Error in identity verification for Emirates ID: {}", onboarding.getEmiratesIdNumber(), e);
            return new RuleVerificationResponseVO(false, BigDecimal.ZERO, SERVICE_NAME, e.getMessage());
        }
    }
}
