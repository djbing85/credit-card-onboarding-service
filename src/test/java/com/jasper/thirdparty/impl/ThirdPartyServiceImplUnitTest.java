package com.jasper.thirdparty.impl;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.thirdparty.*;
import com.jasper.thirdparty.dto.*;
import com.jasper.thirdparty.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.stream.Stream;

/**
 * Unit tests for third-party service implementations
 * Tests each implementation class with various response scenarios
 */
@Slf4j
@SpringBootTest(classes = {
        IdentityVerificationRuleServiceImpl.class,
        BehavioralAnalysisRuleServiceImpl.class,
        ComplianceCheckRuleServiceImpl.class,
        EmploymentVerificationRuleServiceImpl.class,
        RiskEvaluationRuleServiceImpl.class
})
class ThirdPartyServiceImplUnitTest {

    @Autowired
    private IdentityVerificationRuleServiceImpl identityVerificationRuleService;

    @Autowired
    private BehavioralAnalysisRuleServiceImpl behavioralAnalysisRuleService;

    @Autowired
    private ComplianceCheckRuleServiceImpl complianceCheckRuleService;

    @Autowired
    private EmploymentVerificationRuleServiceImpl employmentVerificationRuleService;

    @Autowired
    private RiskEvaluationRuleServiceImpl riskEvaluationRuleService;

    @MockitoBean
    private IdentityVerificationServiceClient identityVerificationServiceClient;

    @MockitoBean
    private BehavioralAnalysisServiceClient behavioralAnalysisServiceClient;

    @MockitoBean
    private ComplianceCheckServiceClient complianceCheckServiceClient;

    @MockitoBean
    private EmploymentVerificationServiceClient employmentVerificationServiceClient;

    @MockitoBean
    private RiskEvaluationServiceClient riskEvaluationServiceClient;

    // ==================== Identity Verification Tests ====================

    static Stream<Arguments> identityScenarios() {
        return Stream.of(
                Arguments.of("identity_pass", true, null),
                Arguments.of("identity_rejected", false, null),
                Arguments.of("identity_exception", null, new RuntimeException("Service error")),
                Arguments.of("identity_connect_timeout", null, new RuntimeException("Connect timeout")),
                Arguments.of("identity_socket_timeOut", null, new RuntimeException("Socket timeout"))
        );
    }

    @ParameterizedTest(name = IdentityVerificationRuleServiceImpl.SERVICE_NAME+": {0}")
    @MethodSource("identityScenarios")
    void testIdentityVerification(String scenarioName, Boolean verified, Exception exception) {
        CreditCardOnboarding onboarding = createTestOnboarding();

        if (exception != null) {
            Mockito.when(identityVerificationServiceClient.verifyIdentity(Mockito.any()))
                    .thenThrow(exception);
        } else {
            IdentityVerificationResponseVO response = new IdentityVerificationResponseVO();
            response.setVerified(verified);
            Mockito.when(identityVerificationServiceClient.verifyIdentity(Mockito.any())).thenReturn(response);
        }

        RuleVerificationResponseVO result = identityVerificationRuleService.verify(onboarding);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(IdentityVerificationRuleServiceImpl.SERVICE_NAME, result.getRuleName());

        if (scenarioName.contains("pass")) {
            Assertions.assertTrue(result.getVerified());
            Assertions.assertEquals(BigDecimal.ONE, result.getRiskScore());
        } else {
            Assertions.assertFalse(result.getVerified());
            Assertions.assertEquals(BigDecimal.ZERO, result.getRiskScore());
        }

        log.info("✓ {} - verified={}, score={}", scenarioName, result.getVerified(), result.getRiskScore());
    }

    // ==================== Behavioral Analysis Tests ====================

    static Stream<Arguments> behavioralScenarios() {
        return Stream.of(
                Arguments.of("behavior_score_0", new BigDecimal("0.0000"), null),
                Arguments.of("behavior_score_0.5", new BigDecimal("0.5000"), null),
                Arguments.of("behavior_score_1", new BigDecimal("1.0000"), null),
                Arguments.of("behavior_exception", null, new RuntimeException("Service error")),
                Arguments.of("behavior_connect_timeout", null, new RuntimeException("Connect timeout")),
                Arguments.of("behavior_socket_timeOut", null, new RuntimeException("Socket timeout"))
        );
    }

    @ParameterizedTest(name = BehavioralAnalysisRuleServiceImpl.SERVICE_NAME+": {0}")
    @MethodSource("behavioralScenarios")
    void testBehavioralAnalysis(String scenarioName, BigDecimal score, Exception exception) {
        CreditCardOnboarding onboarding = createTestOnboarding();

        if (exception != null) {
            Mockito.when(behavioralAnalysisServiceClient.analyzeBehavior(Mockito.any()))
                    .thenThrow(exception);
        } else {
            BehavioralAnalysisResponseVO response = new BehavioralAnalysisResponseVO();
            response.setBehavioralScore(score);
            Mockito.when(behavioralAnalysisServiceClient.analyzeBehavior(Mockito.any())).thenReturn(response);
        }

        RuleVerificationResponseVO result = behavioralAnalysisRuleService.verify(onboarding);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(BehavioralAnalysisRuleServiceImpl.SERVICE_NAME, result.getRuleName());

        if (exception != null) {
            Assertions.assertFalse(result.getVerified());
            Assertions.assertEquals(BigDecimal.ZERO, result.getRiskScore());
            Assertions.assertNotNull(result.getErrorMessage());
        } else {
            Assertions.assertTrue(result.getVerified());
            Assertions.assertEquals(score, result.getRiskScore());
        }

        log.info("✓ {} - verified={}, score={}", scenarioName, result.getVerified(), result.getRiskScore());
    }

    // ==================== Compliance Check Tests ====================

    static Stream<Arguments> complianceScenarios() {
        return Stream.of(
                Arguments.of("compliance_pass", true, null),
                Arguments.of("compliance_rejected", false, null),
                Arguments.of("compliance_exception", null, new RuntimeException("Service error")),
                Arguments.of("compliance_connect_timeout", null, new RuntimeException("Connect timeout")),
                Arguments.of("compliance_socket_timeOut", null, new RuntimeException("Socket timeout"))
        );
    }

    @ParameterizedTest(name = ComplianceCheckRuleServiceImpl.SERVICE_NAME+": {0}")
    @MethodSource("complianceScenarios")
    void testComplianceCheck(String scenarioName, Boolean passed, Exception exception) {
        CreditCardOnboarding onboarding = createTestOnboarding();

        if (exception != null) {
            Mockito.when(complianceCheckServiceClient.checkCompliance(Mockito.any()))
                    .thenThrow(exception);
        } else {
            ComplianceCheckResponseVO response = new ComplianceCheckResponseVO();
            response.setPassed(passed);
            Mockito.when(complianceCheckServiceClient.checkCompliance(Mockito.any())).thenReturn(response);
        }

        RuleVerificationResponseVO result = complianceCheckRuleService.verify(onboarding);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(ComplianceCheckRuleServiceImpl.SERVICE_NAME, result.getRuleName());

        if (scenarioName.contains("pass")) {
            Assertions.assertTrue(result.getVerified());
            Assertions.assertEquals(BigDecimal.ONE, result.getRiskScore());
        } else {
            Assertions.assertFalse(result.getVerified());
            Assertions.assertEquals(BigDecimal.ZERO, result.getRiskScore());
        }

        log.info("✓ {} - verified={}, score={}", scenarioName, result.getVerified(), result.getRiskScore());
    }

    // ==================== Employment Verification Tests ====================

    static Stream<Arguments> employmentScenarios() {
        return Stream.of(
                Arguments.of("employment_pass", true, null),
                Arguments.of("employment_rejected", false, null),
                Arguments.of("employment_exception", null, new RuntimeException("Service error")),
                Arguments.of("employment_connect_timeout", null, new RuntimeException("Connect timeout")),
                Arguments.of("employment_socket_timeOut", null, new RuntimeException("Socket timeout"))
        );
    }

    @ParameterizedTest(name = EmploymentVerificationRuleServiceImpl.SERVICE_NAME+": {0}")
    @MethodSource("employmentScenarios")
    void testEmploymentVerification(String scenarioName, Boolean verified, Exception exception) {
        CreditCardOnboarding onboarding = createTestOnboarding();

        if (exception != null) {
            Mockito.when(employmentVerificationServiceClient.verifyEmployment(Mockito.any()))
                    .thenThrow(exception);
        } else {
            EmploymentVerificationResponseVO response = new EmploymentVerificationResponseVO();
            response.setVerified(verified);
            Mockito.when(employmentVerificationServiceClient.verifyEmployment(Mockito.any())).thenReturn(response);
        }

        RuleVerificationResponseVO result = employmentVerificationRuleService.verify(onboarding);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(EmploymentVerificationRuleServiceImpl.SERVICE_NAME, result.getRuleName());

        if (scenarioName.contains("pass")) {
            Assertions.assertTrue(result.getVerified());
            Assertions.assertEquals(BigDecimal.ONE, result.getRiskScore());
        } else {
            Assertions.assertFalse(result.getVerified());
            Assertions.assertEquals(BigDecimal.ZERO, result.getRiskScore());
        }

        log.info("✓ {} - verified={}, score={}", scenarioName, result.getVerified(), result.getRiskScore());
    }

    // ==================== Risk Evaluation Tests ====================

    static Stream<Arguments> riskScenarios() {
        return Stream.of(
                Arguments.of("risk_score_0", new BigDecimal("0.0000"), null),
                Arguments.of("risk_score_0.5", new BigDecimal("0.5000"), null),
                Arguments.of("risk_score_1", new BigDecimal("1.0000"), null),
                Arguments.of("risk_exception", null, new RuntimeException("Service error")),
                Arguments.of("risk_connect_timeout", null, new RuntimeException("Connect timeout")),
                Arguments.of("risk_socket_timeOut", null, new RuntimeException("Socket timeout"))
        );
    }

    @ParameterizedTest(name = RiskEvaluationRuleServiceImpl.SERVICE_NAME+": {0}")
    @MethodSource("riskScenarios")
    void testRiskEvaluation(String scenarioName, BigDecimal score, Exception exception) {
        CreditCardOnboarding onboarding = createTestOnboarding();

        if (exception != null) {
            Mockito.when(riskEvaluationServiceClient.evaluateRisk(Mockito.any()))
                    .thenThrow(exception);
        } else {
            RiskEvaluationResponseVO response = new RiskEvaluationResponseVO();
            response.setRiskScore(score);
            Mockito.when(riskEvaluationServiceClient.evaluateRisk(Mockito.any())).thenReturn(response);
        }

        RuleVerificationResponseVO result = riskEvaluationRuleService.verify(onboarding);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(RiskEvaluationRuleServiceImpl.SERVICE_NAME, result.getRuleName());

        if (exception != null) {
            Assertions.assertFalse(result.getVerified());
            Assertions.assertEquals(BigDecimal.ZERO, result.getRiskScore());
            Assertions.assertNotNull(result.getErrorMessage());
        } else {
            Assertions.assertTrue(result.getVerified());
            Assertions.assertEquals(score, result.getRiskScore());
        }

        log.info("✓ {} - verified={}, score={}", scenarioName, result.getVerified(), result.getRiskScore());
    }

    // ==================== Helper Methods ====================

    private CreditCardOnboarding createTestOnboarding() {
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber("784-TEST-001");
        onboarding.setName("Test User");
        onboarding.setMobileNumber("+971501234567");
        onboarding.setNationality("UAE");
        onboarding.setAddress("Dubai");
        onboarding.setIncome(new BigDecimal("300000"));
        onboarding.setEmploymentDetails("Software Engineer");
        onboarding.setRequestedCreditLimit(new BigDecimal("50000"));
        onboarding.setBankStatement("Sample bank statement");
        return onboarding;
    }
}
