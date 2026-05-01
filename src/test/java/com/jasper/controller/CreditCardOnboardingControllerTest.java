package com.jasper.controller;

import com.jasper.BaseIntegrationTest;
import com.jasper.common.CommonResponse;
import com.jasper.dto.CreditCardOnboardingDTO;
import com.jasper.entity.CreditCardOnboarding;
import com.jasper.service.CreditCardOnboardingService;
import com.jasper.thirdparty.*;
import com.jasper.thirdparty.factory.CreditCardOnboardingRuleFactory;
import com.jasper.thirdparty.vo.*;
import com.jasper.vo.CreditCardOnboardingVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CreditCardOnboardingController Test
 * Each verify scenario is tested separately with @MethodSource for parameterization
 */
@Slf4j
class CreditCardOnboardingControllerTest extends BaseIntegrationTest {

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CreditCardOnboardingService onboardingService;

    @Autowired
    private CreditCardOnboardingRuleFactory ruleFactory;

    // Mock all 5 third-party service clients
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

    @BeforeEach
    void setUp() {
        // Reset all mocks before each test to avoid interference
        Mockito.reset(
                identityVerificationServiceClient,
                behavioralAnalysisServiceClient,
                complianceCheckServiceClient,
                employmentVerificationServiceClient,
                riskEvaluationServiceClient
        );
    }

    // ==================== Verify Endpoint Tests ====================

    /**
     * Data source for verify scenarios
     * Score range: 0.0 to 1.0 with 0.1 interval (10 scenarios)
     * Rule configuration:
     * - Identity (boolean): score=0.2, contribution=1.0 -> contributes 0.2 if verified
     * - Compliance (boolean): score=0.2, contribution=1.0 -> contributes 0.2 if passed
     * - Employment (boolean): score=0.2, contribution=1.0 -> contributes 0.2 if verified
     * - Risk (decimal): score=0.0, contribution=0.2 -> contributes riskScore * 0.2
     * - Behavioral (decimal): score=0.0, contribution=0.2 -> contributes behavioralScore * 0.2
     * 
     * Parameters format (20 total):
     * 1-8: Basic user info (emiratesId, name, mobile, nationality, address, income, employmentDetails, creditLimit)
     * 9-10: identityVerified (true/false), identityThrowsException (true=throw exception, false=return identityVerified value)
     * 11-12: behavioralScore (BigDecimal), behavioralThrowsException (true=throw exception, false=return behavioralScore value)
     * 13-14: compliancePassed (true/false), complianceThrowsException (true=throw exception, false=return compliancePassed value)
     * 15-16: employmentVerified (true/false), employmentThrowsException (true=throw exception, false=return employmentVerified value)
     * 17-18: riskScore (BigDecimal), riskThrowsException (true=throw exception, false=return riskScore value)
     * 19: expectedResult (REJECTED/MANUAL_REVIEW/MANUAL_REVIEW_LIMIT/AUTO_ISSUE)
     * 20: expectedScore (final calculated score)
     * 
     * Note: When throwsException=false, the service returns the configured value normally,
     *       which can be false/0.0 as valid business responses.
     */
    static Stream<Arguments> verifyScenarios() {
        return Stream.of(
                // Scenario 1: Score 0.0 - All services return false/0.0 normally (no exceptions) -> REJECTED
                Arguments.of(
                        "784-VT-SCORE-000",
                        "Score 0.0 User",
                        "+971500000000",
                        "UAE",
                        "Dubai",
                        new BigDecimal("100000"),
                        "Unemployed",
                        new BigDecimal("10000"),
                        false,          // identityVerified: normal business response = false
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // behavioralScore: normal business response = 0.0
                        false,          // behavioralThrowsException: false = service works normally
                        false,          // compliancePassed: normal business response = false
                        false,          // complianceThrowsException: false = service works normally
                        false,          // employmentVerified: normal business response = false
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // riskScore: normal business response = 0.0
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_REJECTED,
                        new BigDecimal("0.0000")
                ),
                // Scenario 1-2: Score 0.0 - identityThrowsException=true -> ERROR
                Arguments.of(
                        "784-VT-SCORE-000-2",
                        "Score 0.0 User-2",
                        "+971500000000-2",
                        "UAE",
                        "Dubai",
                        new BigDecimal("100000"),
                        "Unemployed",
                        new BigDecimal("10000"),
                        false,          // identityVerified: normal business response = false
                        true,          // identityThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // behavioralScore: normal business response = 0.0
                        false,          // behavioralThrowsException: false = service works normally
                        false,          // compliancePassed: normal business response = false
                        false,          // complianceThrowsException: false = service works normally
                        false,          // employmentVerified: normal business response = false
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // riskScore: normal business response = 0.0
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_ERROR,
                        new BigDecimal("0.0000")
                ),
                // this case will never happen base on current rules
//                // Scenario 2: Score 0.1 - Partial behavioral -> REJECTED
//                Arguments.of(
//                        "784-VT-SCORE-010",
//                        "Score 0.1 User",
//                        "+971501111111",
//                        "UAE",
//                        "Dubai",
//                        new BigDecimal("100000"),
//                        "Intern",
//                        new BigDecimal("10000"),
//                        false,          // identityVerified (score < 0.2, can be false)
//                        new BigDecimal("0.5"),  // behavioralScore: 0.5 * 0.2 = 0.1
//                        false,          // compliancePassed
//                        false,          // employmentVerified
//                        new BigDecimal("0.0"),  // riskScore
//                        CreditCardOnboarding.VERIFIED_RESULT_REJECTED,
//                        new BigDecimal("0.1000")
//                ),
                // Scenario 3: Score 0.2 - Identity returns true, others return false/0.0 normally -> REJECTED
                Arguments.of(
                        "784-VT-SCORE-020",
                        "Score 0.2 User",
                        "+971502222222",
                        "UAE",
                        "Dubai",
                        new BigDecimal("150000"),
                        "Junior Staff",
                        new BigDecimal("15000"),
                        true,           // identityVerified: normal business response = true (contributes 0.2)
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // behavioralScore: normal business response = 0.0 (contributes 0.0)
                        false,          // behavioralThrowsException: false = service works normally
                        false,          // compliancePassed: normal business response = false (contributes 0.0)
                        false,          // complianceThrowsException: false = service works normally
                        false,          // employmentVerified: normal business response = false (contributes 0.0)
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // riskScore: normal business response = 0.0 (contributes 0.0)
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_REJECTED,
                        new BigDecimal("0.2000") // Identity: 0.2 * 1.0 = 0.2
                ),
                // Scenario 3-2: Score 0.2 - Identity returns true, others throws exception -> ERROR
                Arguments.of(
                        "784-VT-SCORE-020-2",
                        "Score 0.2 User",
                        "+971502222222-2",
                        "UAE",
                        "Dubai",
                        new BigDecimal("150000"),
                        "Junior Staff",
                        new BigDecimal("15000"),
                        true,           // identityVerified: normal business response = true (contributes 0.2)
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // behavioralScore: normal business response = 0.0 (contributes 0.0)
                        true,          // behavioralThrowsException: false = service works normally
                        false,          // compliancePassed: normal business response = false (contributes 0.0)
                        true,          // complianceThrowsException: false = service works normally
                        false,          // employmentVerified: normal business response = false (contributes 0.0)
                        true,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // riskScore: normal business response = 0.0 (contributes 0.0)
                        true,          // riskThrowsException: true
                        CreditCardOnboarding.VERIFIED_RESULT_ERROR,
                        new BigDecimal("0.2000") // Identity: 0.2 * 1.0 = 0.2
                ),
                // Scenario 4: Score 0.3 - Identity true + behavioral 0.5, others false -> REJECTED
                Arguments.of(
                        "784-VT-SCORE-030",
                        "Score 0.3 User",
                        "+971503333333",
                        "UAE",
                        "Dubai",
                        new BigDecimal("150000"),
                        "Staff",
                        new BigDecimal("15000"),
                        true,           // identityVerified: normal business response = true
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("0.5"),  // behavioralScore: normal business response = 0.5 (contributes 0.1)
                        false,          // behavioralThrowsException: false = service works normally
                        false,          // compliancePassed: normal business response = false
                        false,          // complianceThrowsException: false = service works normally
                        false,          // employmentVerified: normal business response = false
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // riskScore: normal business response = 0.0
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_REJECTED,
                        new BigDecimal("0.3000") // 0.2 + 0.1 = 0.3
                ),
                // Scenario 5: Score 0.4 - Identity true + compliance true, others false -> REJECTED
                Arguments.of(
                        "784-VT-SCORE-040",
                        "Score 0.4 User",
                        "+971504444444",
                        "UAE",
                        "Dubai",
                        new BigDecimal("200000"),
                        "Analyst",
                        new BigDecimal("20000"),
                        true,           // identityVerified: normal business response = true
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // behavioralScore: normal business response = 0.0
                        false,          // behavioralThrowsException: false = service works normally
                        true,           // compliancePassed: normal business response = true (contributes 0.2)
                        false,          // complianceThrowsException: false = service works normally
                        false,          // employmentVerified: normal business response = false
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // riskScore: normal business response = 0.0
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_REJECTED,
                        new BigDecimal("0.4000") // 0.2 + 0.2 = 0.4
                ),
                // Scenario 6: Score 0.5 - Identity + Compliance + behavioral 0.5 -> MANUAL_REVIEW
                Arguments.of(
                        "784-VT-SCORE-050",
                        "Score 0.5 User",
                        "+971505555555",
                        "UAE",
                        "Dubai",
                        new BigDecimal("200000"),
                        "Analyst",
                        new BigDecimal("25000"),
                        true,           // identityVerified: normal business response = true
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("0.5"),  // behavioralScore: normal business response = 0.5 (contributes 0.1)
                        false,          // behavioralThrowsException: false = service works normally
                        true,           // compliancePassed: normal business response = true (contributes 0.2)
                        false,          // complianceThrowsException: false = service works normally
                        false,          // employmentVerified: normal business response = false
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // riskScore: normal business response = 0.0
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_MANUAL_REVIEW,
                        new BigDecimal("0.5000") // 0.2 + 0.2 + 0.1 = 0.5
                ),
                // Scenario 7: Score 0.6 - Identity + Compliance + Employment -> MANUAL_REVIEW
                Arguments.of(
                        "784-VT-SCORE-060",
                        "Score 0.6 User",
                        "+971506666666",
                        "UAE",
                        "Dubai",
                        new BigDecimal("250000"),
                        "Senior Analyst",
                        new BigDecimal("30000"),
                        true,           // identityVerified: normal business response = true
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // behavioralScore: normal business response = 0.0
                        false,          // behavioralThrowsException: false = service works normally
                        true,           // compliancePassed: normal business response = true (contributes 0.2)
                        false,          // complianceThrowsException: false = service works normally
                        true,           // employmentVerified: normal business response = true (contributes 0.2)
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // riskScore: normal business response = 0.0
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_MANUAL_REVIEW,
                        new BigDecimal("0.6000") // 0.2 + 0.2 + 0.2 = 0.6
                ),
                // Scenario 8: Score 0.7 - Identity + Compliance + Employment + behavioral 0.5 -> MANUAL_REVIEW
                Arguments.of(
                        "784-VT-SCORE-070",
                        "Score 0.7 User",
                        "+971507777777",
                        "UAE",
                        "Dubai",
                        new BigDecimal("250000"),
                        "Senior Analyst",
                        new BigDecimal("35000"),
                        true,           // identityVerified: normal business response = true
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("0.5"),  // behavioralScore: normal business response = 0.5 (contributes 0.1)
                        false,          // behavioralThrowsException: false = service works normally
                        true,           // compliancePassed: normal business response = true (contributes 0.2)
                        false,          // complianceThrowsException: false = service works normally
                        true,           // employmentVerified: normal business response = true (contributes 0.2)
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.0"),  // riskScore: normal business response = 0.0
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_MANUAL_REVIEW,
                        new BigDecimal("0.7000") // 0.2 + 0.2 + 0.2 + 0.1 = 0.7
                ),
                // Scenario 9: Score 0.8 - All pass with partial decimal -> MANUAL_REVIEW_LIMIT
                Arguments.of(
                        "784-VT-SCORE-080",
                        "Score 0.8 User",
                        "+971508888888",
                        "UAE",
                        "Dubai Marina",
                        new BigDecimal("300000"),
                        "Manager",
                        new BigDecimal("40000"),
                        true,           // identityVerified: normal business response = true
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("0.5"),  // behavioralScore: normal business response = 0.5 (contributes 0.1)
                        false,          // behavioralThrowsException: false = service works normally
                        true,           // compliancePassed: normal business response = true (contributes 0.2)
                        false,          // complianceThrowsException: false = service works normally
                        true,           // employmentVerified: normal business response = true (contributes 0.2)
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.5"),  // riskScore: normal business response = 0.5 (contributes 0.1)
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_MANUAL_REVIEW_LIMIT,
                        new BigDecimal("0.8000") // 0.2 + 0.2 + 0.2 + 0.1 + 0.1 = 0.8
                ),
                // Scenario 10: Score 0.9 - All pass with high decimal -> AUTO_ISSUE
                Arguments.of(
                        "784-VT-SCORE-090",
                        "Score 0.9 User",
                        "+971509999999",
                        "UAE",
                        "Palm Jumeirah",
                        new BigDecimal("500000"),
                        "Director",
                        new BigDecimal("80000"),
                        true,           // identityVerified: normal business response = true
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("1.0"),  // behavioralScore: normal business response = 1.0 (contributes 0.2)
                        false,          // behavioralThrowsException: false = service works normally
                        true,           // compliancePassed: normal business response = true (contributes 0.2)
                        false,          // complianceThrowsException: false = service works normally
                        true,           // employmentVerified: normal business response = true (contributes 0.2)
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("0.5"),  // riskScore: normal business response = 0.5 (contributes 0.1)
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_AUTO_ISSUE,
                        new BigDecimal("0.9000") // 0.2 + 0.2 + 0.2 + 0.2 + 0.1 = 0.9
                ),
                // Scenario 11: Score 1.0 - All pass completely -> AUTO_ISSUE
                Arguments.of(
                        "784-VT-SCORE-100",
                        "Score 1.0 User",
                        "+971510000000",
                        "UAE",
                        "Burj Khalifa",
                        new BigDecimal("1000000"),
                        "CEO",
                        new BigDecimal("200000"),
                        true,           // identityVerified: normal business response = true
                        false,          // identityThrowsException: false = service works normally
                        new BigDecimal("1.0"),  // behavioralScore: normal business response = 1.0 (contributes 0.2)
                        false,          // behavioralThrowsException: false = service works normally
                        true,           // compliancePassed: normal business response = true (contributes 0.2)
                        false,          // complianceThrowsException: false = service works normally
                        true,           // employmentVerified: normal business response = true (contributes 0.2)
                        false,          // employmentThrowsException: false = service works normally
                        new BigDecimal("1.0"),  // riskScore: normal business response = 1.0 (contributes 0.2)
                        false,          // riskThrowsException: false = service works normally
                        CreditCardOnboarding.VERIFIED_RESULT_AUTO_ISSUE,
                        new BigDecimal("1.0000") // 0.2 + 0.2 + 0.2 + 0.2 + 0.2 = 1.0
                )
        );
    }

    @ParameterizedTest(name = "Scenario: score {19} - {18} - {0}")
    @MethodSource("verifyScenarios")
    void testVerifyEndpoint(String emiratesId, String name, String mobile, String nationality,
                            String address, BigDecimal income, String employmentDetails,
                            BigDecimal creditLimit, 
                            boolean identityVerified, boolean identityThrowsException,
                            BigDecimal behavioralScore, boolean behavioralThrowsException,
                            boolean compliancePassed, boolean complianceThrowsException,
                            boolean employmentVerified, boolean employmentThrowsException,
                            BigDecimal riskScore, boolean riskThrowsException,
                            String expectedResult, BigDecimal expectedScore) {
        try {
            // 1. Create onboarding application via HTTP POST
            CreditCardOnboardingDTO dto = new CreditCardOnboardingDTO();
            dto.setEmiratesIdNumber(emiratesId);
            dto.setName(name);
            dto.setMobileNumber(mobile);
            dto.setNationality(nationality);
            dto.setAddress(address);
            dto.setIncome(income);
            dto.setEmploymentDetails(employmentDetails);
            dto.setRequestedCreditLimit(creditLimit);

            String createResponse = mockMvc.perform(post("/api/onboarding")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = extractIdFromResponse(createResponse);
            log.info("Created onboarding with ID: {}", id);

            // 2. Mock third-party service clients based on scenario parameters
            // When throwsException=false, services return configured values (including false/0.0 as normal business responses)
            mockThirdPartyServices(identityVerified, identityThrowsException,
                                  behavioralScore, behavioralThrowsException,
                                  compliancePassed, complianceThrowsException,
                                  employmentVerified, employmentThrowsException,
                                  riskScore, riskThrowsException);

            // 3. Call verify endpoint (ruleFactory is real, uses DB rules with ID <= 5)
            String verifyUrl = "/api/onboarding/" + id + "/verify";
            String verifyResponse = mockMvc.perform(post(verifyUrl))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CreditCardOnboarding result = extractOnboardingFromResponse(verifyResponse);

            // 4. Verify response
            Assertions.assertNotNull(result);
            Assertions.assertEquals(expectedResult, result.getVerifiedResult(),
                    "Expected result " + expectedResult + " but got " + result.getVerifiedResult());
            Assertions.assertEquals(expectedScore.toString(), result.getVerifiedScore(),
                    "Expected score " + expectedScore + " but got " + result.getVerifiedScore());
            Assertions.assertNotNull(result.getVerifiedDetail());
            Assertions.assertNotNull(result.getVerifiedTime());

            log.info("Verified onboarding ID: {}, Result: {}, Score: {}",
                    id, result.getVerifiedResult(), result.getVerifiedScore());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Mock all 5 third-party service clients with specified responses
     * 
     * @param identityVerified The value to return for identity verification (true/false)
     * @param identityThrowsException Whether to throw exception instead of returning value
     * @param behavioralScore The BigDecimal score to return for behavioral analysis
     * @param behavioralThrowsException Whether to throw exception instead of returning value
     * @param compliancePassed The value to return for compliance check (true/false)
     * @param complianceThrowsException Whether to throw exception instead of returning value
     * @param employmentVerified The value to return for employment verification (true/false)
     * @param employmentThrowsException Whether to throw exception instead of returning value
     * @param riskScore The BigDecimal score to return for risk evaluation
     * @param riskThrowsException Whether to throw exception instead of returning value
     */
    private void mockThirdPartyServices(boolean identityVerified, boolean identityThrowsException,
                                       BigDecimal behavioralScore, boolean behavioralThrowsException,
                                       boolean compliancePassed, boolean complianceThrowsException,
                                       boolean employmentVerified, boolean employmentThrowsException,
                                       BigDecimal riskScore, boolean riskThrowsException) {
        // Mock Identity Verification Service
        if (identityThrowsException) {
            // Simulate service failure
            Mockito.when(identityVerificationServiceClient.verifyIdentity(Mockito.any()))
                    .thenThrow(new RuntimeException("Identity verification service Mock Exception"));
        } else {
            // Normal business response - can be true OR false
            IdentityVerificationResponseVO identityResponse = new IdentityVerificationResponseVO();
            identityResponse.setVerified(identityVerified);  // Supports both true and false
            log.debug("Mocking identity verification to return: {}", identityVerified);
            Mockito.when(identityVerificationServiceClient.verifyIdentity(Mockito.any()))
                    .thenReturn(identityResponse);
        }

        // Mock Behavioral Analysis Service
        if (behavioralThrowsException) {
            // Simulate service failure
            Mockito.when(behavioralAnalysisServiceClient.analyzeBehavior(Mockito.any()))
                    .thenThrow(new RuntimeException("Behavioral analysis service Mock Exception"));
        } else {
            // Normal business response - can be any BigDecimal including 0.0
            BehavioralAnalysisResponseVO behaviorResponse = new BehavioralAnalysisResponseVO();
            behaviorResponse.setBehavioralScore(behavioralScore);  // Supports 0.0 and other values
            log.debug("Mocking behavioral analysis to return score: {}", behavioralScore);
            Mockito.when(behavioralAnalysisServiceClient.analyzeBehavior(Mockito.any()))
                    .thenReturn(behaviorResponse);
        }

        // Mock Compliance Check Service
        if (complianceThrowsException) {
            // Simulate service failure
            Mockito.when(complianceCheckServiceClient.checkCompliance(Mockito.any()))
                    .thenThrow(new RuntimeException("Compliance check service Mock Exception"));
        } else {
            // Normal business response - can be true OR false
            ComplianceCheckResponseVO complianceResponse = new ComplianceCheckResponseVO();
            complianceResponse.setPassed(compliancePassed);  // Supports both true and false
            log.debug("Mocking compliance check to return: {}", compliancePassed);
            Mockito.when(complianceCheckServiceClient.checkCompliance(Mockito.any()))
                    .thenReturn(complianceResponse);
        }

        // Mock Employment Verification Service
        if (employmentThrowsException) {
            // Simulate service failure
            Mockito.when(employmentVerificationServiceClient.verifyEmployment(Mockito.any()))
                    .thenThrow(new RuntimeException("Employment verification service Mock Exception"));
        } else {
            // Normal business response - can be true OR false
            EmploymentVerificationResponseVO employmentResponse = new EmploymentVerificationResponseVO();
            employmentResponse.setVerified(employmentVerified);  // Supports both true and false
            log.debug("Mocking employment verification to return: {}", employmentVerified);
            Mockito.when(employmentVerificationServiceClient.verifyEmployment(Mockito.any()))
                    .thenReturn(employmentResponse);
        }

        // Mock Risk Evaluation Service
        if (riskThrowsException) {
            // Simulate service failure
            Mockito.when(riskEvaluationServiceClient.evaluateRisk(Mockito.any()))
                    .thenThrow(new RuntimeException("Risk evaluation service Mock Exception"));
        } else {
            // Normal business response - can be any BigDecimal including 0.0
            RiskEvaluationResponseVO riskResponse = new RiskEvaluationResponseVO();
            riskResponse.setRiskScore(riskScore);  // Supports 0.0 and other values
            log.debug("Mocking risk evaluation to return score: {}", riskScore);
            Mockito.when(riskEvaluationServiceClient.evaluateRisk(Mockito.any()))
                    .thenReturn(riskResponse);
        }
    }

    /**
     * Test verify endpoint with non-existent ID to cover notFound case
     */
    @Test
    void testVerifyNotFound() {
        try {
            // Try to verify a non-existent onboarding application
            String verifyUrl = "/api/onboarding/999999/verify";
            String verifyResponse = mockMvc.perform(post(verifyUrl))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Parse the response
            tools.jackson.core.type.TypeReference<CommonResponse<Void>> typeRef = 
                new tools.jackson.core.type.TypeReference<CommonResponse<Void>>() {};
            CommonResponse<Void> response = jsonMapper.readValue(verifyResponse, typeRef);

            // Verify the response
            Assertions.assertNotNull(response);
            Assertions.assertEquals(CommonResponse.CODE_NOT_FOUND, response.getCode());
            Assertions.assertEquals(CommonResponse.CODE_NOT_FOUND, response.getErrorCode());
            Assertions.assertEquals("Onboarding application not found", response.getMessage());

            log.info("Verify non-existent ID returns 404 as expected");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to extract CreditCardOnboarding from CommonResponse
     */
    private CreditCardOnboarding extractOnboardingFromResponse(String jsonResponse) throws Exception {
        tools.jackson.core.type.TypeReference<CommonResponse<CreditCardOnboarding>> typeRef = 
            new tools.jackson.core.type.TypeReference<CommonResponse<CreditCardOnboarding>>() {};
        CommonResponse<CreditCardOnboarding> response = jsonMapper.readValue(jsonResponse, typeRef);
        return response != null ? response.getData() : null;
    }

    /**
     * Helper method to extract CreditCardOnboardingVO from CommonResponse
     */
    private CreditCardOnboardingVO extractOnboardingVOFromResponse(String jsonResponse) throws Exception {
        tools.jackson.core.type.TypeReference<CommonResponse<CreditCardOnboardingVO>> typeRef = 
            new tools.jackson.core.type.TypeReference<CommonResponse<CreditCardOnboardingVO>>() {};
        CommonResponse<CreditCardOnboardingVO> response = jsonMapper.readValue(jsonResponse, typeRef);
        return response != null ? response.getData() : null;
    }

    /**
     * Helper method to extract ID from CommonResponse
     */
    private Long extractIdFromResponse(String jsonResponse) throws Exception {
        tools.jackson.core.type.TypeReference<CommonResponse<CreditCardOnboarding>> typeRef = 
            new tools.jackson.core.type.TypeReference<CommonResponse<CreditCardOnboarding>>() {};
        CommonResponse<CreditCardOnboarding> response = jsonMapper.readValue(jsonResponse, typeRef);
        if (response == null || response.getData() == null) {
            return null;
        }
        return response.getData().getId();
    }

    /**
     * Helper method to create mock service
     */
    private RuleVerificationService mockService(boolean pass) throws Exception {
        RuleVerificationService mockService = Mockito.mock(RuleVerificationService.class);
        if (pass) {
            Mockito.when(mockService.verify(Mockito.any()))
                    .thenReturn(new RuleVerificationResponseVO(true, BigDecimal.ONE, "TestRule", null));
        } else {
            Mockito.when(mockService.verify(Mockito.any()))
                    .thenReturn(new RuleVerificationResponseVO(false, BigDecimal.ZERO, "TestRule", null));
        }
        return mockService;
    }

    // ==================== Get By ID Tests ====================

    @Test
    void testGetById() {
        try {
            // Create first
            CreditCardOnboardingDTO dto = new CreditCardOnboardingDTO();
            dto.setEmiratesIdNumber("784-GET-TEST-001");
            dto.setName("Get Test User");
            dto.setMobileNumber("+971501234567");
            dto.setNationality("UAE");
            dto.setAddress("Dubai Marina");
            dto.setIncome(new BigDecimal("300000"));
            dto.setEmploymentDetails("Software Engineer");
            dto.setRequestedCreditLimit(new BigDecimal("50000"));

            String createResponse = mockMvc.perform(post("/api/onboarding")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = extractIdFromResponse(createResponse);

            // Get by ID
            String getResponse = mockMvc.perform(get("/api/onboarding/" + id))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CreditCardOnboarding result = extractOnboardingFromResponse(getResponse);
            Assertions.assertNotNull(result);
            Assertions.assertEquals("784-GET-TEST-001", result.getEmiratesIdNumber());
            Assertions.assertEquals("Get Test User", result.getName());

            log.info("Retrieved onboarding by ID: {}", id);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetByIdNotFound() {
        try {
            mockMvc.perform(get("/api/onboarding/999999"))
                    .andExpect(status().isNotFound());
            log.info("Get non-existent ID returns 404 as expected");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== Page List Tests ====================

    /**
     * Data source for page list scenarios
     */
    static Stream<Arguments> pageListScenarios() {
        return Stream.of(
                // Scenario 1: First page with small size
                Arguments.of(1, 2),
                // Scenario 2: Second page
                Arguments.of(2, 2),
                // Scenario 3: Large page size
                Arguments.of(1, 100),
                // Scenario 4: no data page
                Arguments.of(100, 100)
        );
    }

    @ParameterizedTest(name = "Page List: pageNum={0}, pageSize={1}")
    @MethodSource("pageListScenarios")
    void testPageList(Integer pageNum, Integer pageSize) {
        try {
            // Create multiple records with unique Emirates IDs to avoid duplicate key errors
            int createdCount = 5;
            long timestamp = System.currentTimeMillis();
            for (int i = 0; i < createdCount; i++) {
                CreditCardOnboardingDTO dto = new CreditCardOnboardingDTO();
                dto.setEmiratesIdNumber("784-PAGE-" + timestamp + "-" + String.format("%03d", i));
                dto.setName("Page Test User " + i);
                dto.setMobileNumber("+97150" + String.format("%07d", i));
                dto.setNationality("UAE");
                dto.setAddress("Dubai");
                dto.setIncome(new BigDecimal("200000"));
                dto.setEmploymentDetails("Employee");
                dto.setRequestedCreditLimit(new BigDecimal("30000"));

                mockMvc.perform(post("/api/onboarding")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(dto)))
                        .andExpect(status().isOk());
            }

            // Page query
            String response = mockMvc.perform(get("/api/onboarding/page")
                            .param("pageNum", String.valueOf(pageNum))
                            .param("pageSize", String.valueOf(pageSize)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            tools.jackson.core.type.TypeReference<CommonResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CreditCardOnboarding>>> typeRef = 
                new tools.jackson.core.type.TypeReference<CommonResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CreditCardOnboarding>>>() {};
            CommonResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CreditCardOnboarding>> pageResponse = jsonMapper.readValue(response, typeRef);
            
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<CreditCardOnboarding> page = pageResponse.getData();
            
            // Strict assertions
            Assertions.assertNotNull(page, "Page should not be null");
            Assertions.assertNotNull(page.getRecords(), "Records should not be null");
            Assertions.assertNotNull(page.getTotal(), "Total should not be null");
            Assertions.assertEquals(pageNum.longValue(), page.getCurrent(), 
                    "Current page should match requested page");
            Assertions.assertEquals(pageSize.longValue(), page.getSize(), 
                    "Page size should match requested size");
            
            // Verify total count is at least the number of records we created
            long total = page.getTotal();
            Assertions.assertTrue(total >= createdCount, 
                    String.format("Total (%d) should be at least %d (created records)", total, createdCount));
            
            // Calculate expected pages (ceiling division)
            long expectedPages = (total + pageSize - 1) / pageSize;
            Assertions.assertEquals(expectedPages, page.getPages(), 
                    String.format("Pages (%d) should match calculated value based on total=%d and pageSize=%d", 
                            page.getPages(), total, pageSize));
            
            // Verify record count for this specific page
            int recordCount = page.getRecords().size();
            int expectedRecordCount;
            if (pageNum > page.getPages()) {
                // Beyond last page - should return empty
                expectedRecordCount = 0;
            } else {
                // Calculate expected records for this page
                long recordsBeforeThisPage = (long)(pageNum - 1) * pageSize;
                long remainingRecords = total - recordsBeforeThisPage;
                expectedRecordCount = (int)Math.min(pageSize, Math.max(0, remainingRecords));
            }
            
            Assertions.assertEquals(expectedRecordCount, recordCount,
                    String.format("Record count (%d) should match expected (%d) for page %d/%d with total=%d", 
                            recordCount, expectedRecordCount, pageNum, page.getPages(), total));
            
            // Verify records are ordered by ID descending (if there are multiple records)
            if (recordCount > 1) {
                Long previousId = null;
                for (CreditCardOnboarding record : page.getRecords()) {
                    if (previousId != null) {
                        Assertions.assertTrue(record.getId() <= previousId,
                                "Records should be ordered by ID descending");
                    }
                    previousId = record.getId();
                }
            }
            
            log.info("✓ Page {}/{} returned {} records, total: {}, pageSize: {}", 
                    pageNum, page.getPages(), recordCount, total, pageSize);

        } catch (Exception e) {
            log.error("Page list test failed for pageNum={}, pageSize={}", pageNum, pageSize, e);
            throw new RuntimeException(e);
        }
    }

    // ==================== Update Tests ====================

    /**
     * Data source for update scenarios
     */
    static Stream<Arguments> updateScenarios() {
        return Stream.of(
                // Scenario 1: Update income
                Arguments.of("income", new BigDecimal("500000")),
                // Scenario 2: Update credit limit
                Arguments.of("creditLimit", new BigDecimal("100000")),
                // Scenario 3: Update address
                Arguments.of("address", "Abu Dhabi City Center")
        );
    }

    @ParameterizedTest(name = "Update: field={0}")
    @MethodSource("updateScenarios")
    void testUpdate(String updateField, Object newValue) {
        try {
            // Step 1: Create first using POST
            CreditCardOnboardingDTO createDto = new CreditCardOnboardingDTO();
            createDto.setEmiratesIdNumber("784-UPDATE-TEST-" + System.currentTimeMillis());
            createDto.setName("Original Name");
            createDto.setMobileNumber("+971501234567");
            createDto.setNationality("UAE");
            createDto.setAddress("Original Address");
            createDto.setIncome(new BigDecimal("300000"));
            createDto.setEmploymentDetails("Original Job");
            createDto.setRequestedCreditLimit(new BigDecimal("50000"));

            String createResponse = mockMvc.perform(post("/api/onboarding")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = extractIdFromResponse(createResponse);
            log.info("Created onboarding with ID: {}", id);

            // Step 2: Get current data to have all fields including version
            String getResponse = mockMvc.perform(get("/api/onboarding/" + id))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CreditCardOnboardingVO voToUpdate = extractOnboardingVOFromResponse(getResponse);

            // Step 3: Prepare update DTO with modified field
            CreditCardOnboardingDTO updateDto = new CreditCardOnboardingDTO();
            updateDto.setId(voToUpdate.getId());
            updateDto.setEmiratesIdNumber(voToUpdate.getEmiratesIdNumber());
            updateDto.setName(voToUpdate.getName());
            updateDto.setMobileNumber(voToUpdate.getMobileNumber());
            updateDto.setNationality(voToUpdate.getNationality());
            updateDto.setAddress(voToUpdate.getAddress());
            updateDto.setIncome(voToUpdate.getIncome());
            updateDto.setEmploymentDetails(voToUpdate.getEmploymentDetails());
            updateDto.setRequestedCreditLimit(voToUpdate.getRequestedCreditLimit());
            updateDto.setBankStatement(voToUpdate.getBankStatement());

            // Update specific field based on scenario
            if ("income".equals(updateField)) {
                updateDto.setIncome((BigDecimal) newValue);
            } else if ("creditLimit".equals(updateField)) {
                updateDto.setRequestedCreditLimit((BigDecimal) newValue);
            } else if ("address".equals(updateField)) {
                updateDto.setAddress((String) newValue);
            }

            // Step 4: Call update endpoint via HTTP PUT
            String updateResponse = mockMvc.perform(put("/api/onboarding")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Step 5: Verify the update response
            CreditCardOnboardingVO updatedVo = extractOnboardingVOFromResponse(updateResponse);
            Assertions.assertNotNull(updatedVo);
            Assertions.assertEquals(id, updatedVo.getId());

            if ("income".equals(updateField)) {
                Assertions.assertEquals(newValue, updatedVo.getIncome());
            } else if ("creditLimit".equals(updateField)) {
                Assertions.assertEquals(newValue, updatedVo.getRequestedCreditLimit());
            } else if ("address".equals(updateField)) {
                Assertions.assertEquals(newValue, updatedVo.getAddress());
            }

            log.info("Updated onboarding ID: {}, field: {}, new value: {}", id, updateField, newValue);

        } catch (Exception e) {
            log.error("Update test failed for field: {}", updateField, e);
            throw new RuntimeException(e);
        }
    }

    // ==================== Delete Tests ====================

    @Test
    void testDelete() {
        try {
            // Create first
            CreditCardOnboardingDTO dto = new CreditCardOnboardingDTO();
            dto.setEmiratesIdNumber("784-DELETE-TEST-001");
            dto.setName("Delete Test User");
            dto.setMobileNumber("+971501234567");
            dto.setNationality("UAE");
            dto.setAddress("Dubai");
            dto.setIncome(new BigDecimal("300000"));
            dto.setEmploymentDetails("Employee");
            dto.setRequestedCreditLimit(new BigDecimal("50000"));

            String createResponse = mockMvc.perform(post("/api/onboarding")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = extractIdFromResponse(createResponse);

            // Delete - returns CommonResponse with code=0, HTTP 200
            String deleteResponse = mockMvc.perform(delete("/api/onboarding/" + id))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            tools.jackson.core.type.TypeReference<CommonResponse<Void>> voidTypeRef = 
                new tools.jackson.core.type.TypeReference<CommonResponse<Void>>() {};
            CommonResponse<Void> deleteResult = jsonMapper.readValue(deleteResponse, voidTypeRef);
            Assertions.assertEquals(CommonResponse.CODE_SUCCESS, deleteResult.getCode());

            // Verify deleted - should return CommonResponse with code=404, HTTP 404
            String notFoundResponse = mockMvc.perform(get("/api/onboarding/" + id))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            CommonResponse<Void> notFoundResult = jsonMapper.readValue(notFoundResponse, voidTypeRef);
            Assertions.assertEquals(CommonResponse.CODE_NOT_FOUND, notFoundResult.getCode());

            log.info("Deleted onboarding ID: {}", id);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteNotFound() {
        try {
            mockMvc.perform(delete("/api/onboarding/999999"))
                    .andExpect(status().isNotFound());
            log.info("Delete non-existent ID returns 404 as expected");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== Duplicate Key Tests ====================

    /**
     * Test duplicate Emirates ID number on create
     */
    @Test
    void testCreateDuplicateEmiratesId() {
        try {
            String duplicateEmiratesId = "784-DUPLICATE-TEST-001";
            
            // Create first record
            CreditCardOnboardingDTO dto1 = new CreditCardOnboardingDTO();
            dto1.setEmiratesIdNumber(duplicateEmiratesId);
            dto1.setName("First User");
            dto1.setMobileNumber("+971501111111");
            dto1.setNationality("UAE");
            dto1.setAddress("Dubai");
            dto1.setIncome(new BigDecimal("300000"));
            dto1.setEmploymentDetails("Engineer");
            dto1.setRequestedCreditLimit(new BigDecimal("50000"));

            String createResponse1 = mockMvc.perform(post("/api/onboarding")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(dto1)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id1 = extractIdFromResponse(createResponse1);
            log.info("Created first record with ID: {}", id1);

            // Try to create second record with same Emirates ID - should fail with 409
            CreditCardOnboardingDTO dto2 = new CreditCardOnboardingDTO();
            dto2.setEmiratesIdNumber(duplicateEmiratesId);  // Same ID
            dto2.setName("Second User");
            dto2.setMobileNumber("+971502222222");
            dto2.setNationality("UAE");
            dto2.setAddress("Abu Dhabi");
            dto2.setIncome(new BigDecimal("400000"));
            dto2.setEmploymentDetails("Manager");
            dto2.setRequestedCreditLimit(new BigDecimal("60000"));

            String createResponse2 = mockMvc.perform(post("/api/onboarding")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(dto2)))
                    .andExpect(status().isConflict())  // HTTP 409 Conflict
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Verify error response
            tools.jackson.core.type.TypeReference<CommonResponse<Void>> typeRef = 
                new tools.jackson.core.type.TypeReference<CommonResponse<Void>>() {};
            CommonResponse<Void> errorResponse = jsonMapper.readValue(createResponse2, typeRef);
            
            Assertions.assertEquals(409, errorResponse.getErrorCode());
            Assertions.assertTrue(errorResponse.getMessage().contains("Emirates ID") || 
                                  errorResponse.getMessage().contains("Duplicate"));
            
            log.info("Duplicate key test passed: {}", errorResponse.getMessage());

        } catch (Exception e) {
            log.error("Duplicate key test failed", e);
            throw new RuntimeException(e);
        }
    }

    // ==================== Exception Handling Tests ====================

    /**
     * Data source for exception scenarios to cover GlobalExceptionHandler
     */
    static Stream<Arguments> exceptionScenarios() {
        return Stream.of(
                // Scenario 1: IllegalArgumentException - should return 400
                Arguments.of(
                        "IllegalArgumentException",
                        "Invalid input parameter",
                        400,
                        "Invalid input parameter"
                ),
                // Scenario 2: RuntimeException - should return 500
                Arguments.of(
                        "RuntimeException",
                        "Internal server error occurred",
                        500,
                        "Internal server error: Internal server error occurred"
                ),
                // Scenario 3: General Exception wrapped in RuntimeException - should return 500
                Arguments.of(
                        "RuntimeException_General",
                        "Unexpected error",
                        500,
                        "Internal server error: Unexpected error"
                )
        );
    }

    @ParameterizedTest(name = "Exception Scenario: {0}")
    @MethodSource("exceptionScenarios")
    void testExceptionHandling(String exceptionType, String message, int expectedErrorCode, String expectedMessage) throws Exception {
        // Determine the endpoint based on exception type
        String endpoint;
        switch (exceptionType) {
            case "IllegalArgumentException":
                endpoint = "/api/test/exceptions/illegal-argument";
                break;
            case "RuntimeException":
                endpoint = "/api/test/exceptions/runtime";
                break;
            default:
                endpoint = "/api/test/exceptions/general";
                break;
        }

        // Perform the request and capture the result
        String responseBody = mockMvc.perform(post(endpoint)
                        .param("message", message)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedErrorCode == 400 ? 400 : 500))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Parse the response
        tools.jackson.core.type.TypeReference<CommonResponse<Void>> typeRef = 
            new tools.jackson.core.type.TypeReference<CommonResponse<Void>>() {};
        CommonResponse<Void> response = jsonMapper.readValue(responseBody, typeRef);

        // Assert the response content
        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedErrorCode, response.getErrorCode());
        Assertions.assertTrue(response.getMessage().contains(expectedMessage) || 
                             response.getMessage().equals(expectedMessage),
                             "Expected message containing '" + expectedMessage + "' but got '" + response.getMessage() + "'");
        
        log.info("Exception handled correctly: {} - Error Code: {}, Message: {}", 
                exceptionType, response.getErrorCode(), response.getMessage());
    }

    /**
     * Test validation exceptions using controller endpoint with invalid data
     */
    @ParameterizedTest(name = "Validation Exception Scenario: {0}")
    @MethodSource("validationExceptionScenarios")
    void testValidationExceptionHandling(String scenarioName, String requestBody, int expectedErrorCode, String expectedMessagePattern) throws Exception {
        // Perform the request with invalid data
        String responseBody = mockMvc.perform(post("/api/onboarding")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Parse the response
        tools.jackson.core.type.TypeReference<CommonResponse<Void>> typeRef = 
            new tools.jackson.core.type.TypeReference<CommonResponse<Void>>() {};
        CommonResponse<Void> response = jsonMapper.readValue(responseBody, typeRef);

        // Assert the response content
        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedErrorCode, response.getErrorCode());
        Assertions.assertTrue(response.getMessage().matches(expectedMessagePattern) || 
                             response.getMessage().contains(expectedMessagePattern.replace(".*", "")),
                             "Expected message matching '" + expectedMessagePattern + "' but got '" + response.getMessage() + "'");
        
        log.info("Validation exception handled correctly: {} - Message: {}", 
                scenarioName, response.getMessage());
    }

    /**
     * Data source for validation exception scenarios
     */
    static Stream<Arguments> validationExceptionScenarios() {
        return Stream.of(
                // Scenario 1: Missing required field - emiratesIdNumber (will trigger MethodArgumentNotValidException)
                Arguments.of(
                        "Missing Emirates ID",
                        "{\"name\": \"Test User\", \"mobileNumber\": \"+971501234567\", \"nationality\": \"UAE\", \"address\": \"Dubai\", \"income\": 300000, \"employmentDetails\": \"Employee\", \"requestedCreditLimit\": 50000}",
                        400,
                        ".*Emirates ID number is required.*"
                ),
                // Scenario 2: Missing multiple required fields
                Arguments.of(
                        "Missing Multiple Fields",
                        "{}",
                        400,
                        ".*required.*"
                ),
                // Scenario 3: Invalid income (negative value)
                Arguments.of(
                        "Invalid Income",
                        "{\"emiratesIdNumber\": \"784-TEST-VALIDATION\", \"name\": \"Test User\", \"mobileNumber\": \"+971501234567\", \"nationality\": \"UAE\", \"address\": \"Dubai\", \"income\": -100, \"employmentDetails\": \"Employee\", \"requestedCreditLimit\": 50000}",
                        400,
                        ".*Annual income must be non-negative.*"
                )
        );
    }
}
