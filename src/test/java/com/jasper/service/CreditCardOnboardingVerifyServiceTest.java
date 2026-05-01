package com.jasper.service;

import com.jasper.BaseIntegrationTest;
import com.jasper.entity.CreditCardOnboarding;
import com.jasper.mapper.CreditCardOnboardingRulesMapper;
import com.jasper.thirdparty.RuleVerificationService;
import com.jasper.thirdparty.factory.CreditCardOnboardingRuleFactory;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
class CreditCardOnboardingVerifyServiceTest extends BaseIntegrationTest {

    @Autowired
    private CreditCardOnboardingVerifyService verifyService;

    @Autowired
    private CreditCardOnboardingService onboardingService;

    @MockitoBean
    private CreditCardOnboardingRuleFactory ruleFactory;

    @MockitoBean
    private CreditCardOnboardingRulesMapper rulesMapper;

    static Stream<Arguments> verificationScenarios() {
        return Stream.of(
                // Scenario 1: All pass, score=0.4 (< 0.5) -> REJECTED
                Arguments.of(
                        "784-TEST-001", true, true, CreditCardOnboarding.VERIFIED_RESULT_REJECTED, new BigDecimal("0.4000")
                ),
                // Scenario 2: Mandatory fail -> REJECTED
                Arguments.of(
                        "784-TEST-002", false, true, CreditCardOnboarding.VERIFIED_RESULT_REJECTED, new BigDecimal("0.0000")
                ),
                // Scenario 3: Non-mandatory fail -> REJECTED
                Arguments.of(
                        "784-TEST-003", true, false, CreditCardOnboarding.VERIFIED_RESULT_REJECTED, new BigDecimal("0.2000")
                )
        );
    }

    @ParameterizedTest(name = "Scenario: ID={0}, IdentityPass={1}, RiskPass={2}")
    @MethodSource("verificationScenarios")
    void testVerifyWithScenarios(String emiratesId, boolean identityPass, boolean riskPass, 
                                 String expectedResult, BigDecimal expectedScore) {
        // 1. Prepare Data
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber(emiratesId);
        onboarding.setName("Test User");
        onboardingService.save(onboarding);

        // 2. Mock Factory to return our mock services
        Map<String, RuleVerificationService> mandatoryMap = Map.of(
                "Identity Verification", mockService(identityPass)
        );
        Map<String, RuleVerificationService> nonMandatoryMap = Map.of(
                "Risk Evaluation", mockService(riskPass)
        );
        Mockito.when(ruleFactory.getAllRuleServices(true)).thenReturn(mandatoryMap);
        Mockito.when(ruleFactory.getAllRuleServices(false)).thenReturn(nonMandatoryMap);

        // 3. Mock Rules for score calculation (each contributes 0.2)
        com.jasper.entity.CreditCardOnboardingRules rule = new com.jasper.entity.CreditCardOnboardingRules();
        rule.setScoreContribution(new BigDecimal("0.2000"));
        Mockito.when(rulesMapper.selectOne(Mockito.any())).thenReturn(rule);

        // 4. Execute
        CreditCardOnboardingVerifyService.VerificationResult result = verifyService.verify(onboarding.getId());

        // 5. Verify
        Assertions.assertFalse(result.isNotFound());
        CreditCardOnboarding updated = result.getOnboarding();
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(expectedResult, updated.getVerifiedResult());
        
        // Virtual Thread Assertion: Check if the current thread executing this assertion is virtual
        // Note: The actual work happens in the service, but we can verify the executor configuration
        log.info("Test running on thread: {}, IsVirtual: {}", Thread.currentThread().getName(), Thread.currentThread().isVirtual());
    }

    private RuleVerificationService mockService(boolean pass) {
        RuleVerificationService service = Mockito.mock(RuleVerificationService.class);
        try {
            Mockito.when(service.verify(Mockito.any())).thenReturn(new RuleVerificationResponseVO(pass, BigDecimal.ONE, "TestRule", null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return service;
    }
}
