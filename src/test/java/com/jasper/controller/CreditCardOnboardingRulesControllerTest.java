package com.jasper.controller;

import com.jasper.BaseIntegrationTest;
import com.jasper.common.CommonResponse;
import com.jasper.dto.CreditCardOnboardingRulesDTO;
import com.jasper.entity.CreditCardOnboardingRules;
import com.jasper.service.CreditCardOnboardingRulesService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CreditCardOnboardingRulesController Test
 * Each HTTP endpoint is tested separately with multiple scenarios using @MethodSource
 */
@Slf4j
class CreditCardOnboardingRulesControllerTest extends BaseIntegrationTest {

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CreditCardOnboardingRulesService rulesService;

    private static final String BASE_URL = "/api/rules";

    /**
     * Helper method to extract CreditCardOnboardingRules from CommonResponse
     */
    private CreditCardOnboardingRules extractRuleFromResponse(String jsonResponse) throws Exception {
        tools.jackson.core.type.TypeReference<CommonResponse<CreditCardOnboardingRules>> typeRef = 
            new tools.jackson.core.type.TypeReference<CommonResponse<CreditCardOnboardingRules>>() {};
        CommonResponse<CreditCardOnboardingRules> response = jsonMapper.readValue(jsonResponse, typeRef);
        return response != null ? response.getData() : null;
    }

    /**
     * Helper method to extract ID from CommonResponse
     */
    private Integer extractIdFromResponse(String jsonResponse) throws Exception {
        tools.jackson.core.type.TypeReference<CommonResponse<CreditCardOnboardingRules>> typeRef = 
            new tools.jackson.core.type.TypeReference<CommonResponse<CreditCardOnboardingRules>>() {};
        CommonResponse<CreditCardOnboardingRules> response = jsonMapper.readValue(jsonResponse, typeRef);
        if (response == null || response.getData() == null) {
            return null;
        }
        return response.getData().getId();
    }

    // ==================== Create Rule Tests ====================

    /**
     * Data source for create rule scenarios
     */
    static Stream<Arguments> createRuleScenarios() {
        return Stream.of(
                // Scenario 1: Mandatory rule
                Arguments.of(
                        "Identity Verification", true, 0.2000, 0, 0.20
                ),
                // Scenario 2: Non-mandatory rule
                Arguments.of(
                        "Risk Evaluation", false, 0.3000, 1, 0.30
                ),
                // Scenario 3: Zero score rule
                Arguments.of(
                        "Optional Check", false, 0.0000, 0, 0.00
                )
        );
    }

    @ParameterizedTest(name = "Create Rule: {0}, Mandatory={1}")
    @MethodSource("createRuleScenarios")
    void testCreateRule(String criteria, boolean mandatoryPass, double contribution, 
                        int scoreType, double score) {
        try {
            // Use DTO instead of hardcoded JSON string
            CreditCardOnboardingRulesDTO dto = new CreditCardOnboardingRulesDTO();
            dto.setCriteria(criteria);
            dto.setMandatoryPass(mandatoryPass);
            dto.setScoreContribution(new BigDecimal(String.valueOf(contribution)));
            dto.setScoreType(scoreType);
            dto.setScore(new BigDecimal(String.valueOf(score)));

            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Integer id = extractIdFromResponse(response);

            Assertions.assertNotNull(id);
            log.info("Created rule via HTTP: ID={}, Criteria={}", id, criteria);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== Get By ID Tests ====================

    @Test
    void testGetRuleById() {
        try {
            // Create a rule first
            CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
            rule.setCriteria("Get Test Rule");
            rule.setMandatoryPass(true);
            rule.setScoreContribution(new BigDecimal("0.1500"));
            rule.setScoreType(0);
            rule.setScore(new BigDecimal("15.00"));
            rulesService.create(rule);

            // Get by ID via HTTP
            String response = mockMvc.perform(get(BASE_URL + "/" + rule.getId()))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CreditCardOnboardingRules found = extractRuleFromResponse(response);

            Assertions.assertNotNull(found);
            Assertions.assertEquals("Get Test Rule", found.getCriteria());
            log.info("Retrieved rule via HTTP: ID={}", found.getId());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRuleByIdNotFound() {
        try {
            mockMvc.perform(get(BASE_URL + "/999999"))
                    .andExpect(status().isNotFound());
            log.info("Get non-existent rule returns 404 as expected");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== Update Rule Tests ====================

    /**
     * Data source for update rule scenarios
     */
    static Stream<Arguments> updateRuleScenarios() {
        return Stream.of(
                // Scenario 1: Update score
                Arguments.of("score", new BigDecimal("50.00")),
                // Scenario 2: Update to zero
                Arguments.of("zero", BigDecimal.ZERO),
                // Scenario 3: High score
                Arguments.of("high", new BigDecimal("100.00"))
        );
    }

    @ParameterizedTest(name = "Update Rule: {0}")
    @MethodSource("updateRuleScenarios")
    void testUpdateRule(String scenario, BigDecimal newScore) {
        try {
            // Create a rule first
            CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
            rule.setCriteria("Update Test Rule");
            rule.setMandatoryPass(false);
            rule.setScoreContribution(new BigDecimal("0.2000"));
            rule.setScoreType(0);
            rule.setScore(new BigDecimal("20.00"));
            rulesService.create(rule);

            // Get current data to have all fields
            String getResponse = mockMvc.perform(get(BASE_URL + "/" + rule.getId()))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CreditCardOnboardingRules entityToUpdate = extractRuleFromResponse(getResponse);

            // Update score
            entityToUpdate.setScore(newScore);

            String response = mockMvc.perform(put(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(entityToUpdate)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CreditCardOnboardingRules updated = extractRuleFromResponse(response);
            Assertions.assertEquals(newScore.doubleValue(), updated.getScore().doubleValue(), 0.01);
            log.info("Updated rule via HTTP: ID={}, New Score={}", updated.getId(), newScore);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== Delete Rule Tests ====================

    @Test
    void testDeleteRule() {
        try {
            // Create a rule first
            CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
            rule.setCriteria("Delete Test Rule");
            rule.setMandatoryPass(false);
            rule.setScoreContribution(BigDecimal.ZERO);
            rule.setScoreType(0);
            rule.setScore(BigDecimal.ZERO);
            rulesService.create(rule);

            // Delete via HTTP - returns CommonResponse with code=0, HTTP 200
            String deleteResponse = mockMvc.perform(delete(BASE_URL + "/" + rule.getId()))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            tools.jackson.core.type.TypeReference<CommonResponse<Void>> voidTypeRef = 
                new tools.jackson.core.type.TypeReference<CommonResponse<Void>>() {};
            CommonResponse<Void> deleteResult = jsonMapper.readValue(deleteResponse, voidTypeRef);
            Assertions.assertEquals(CommonResponse.CODE_SUCCESS, deleteResult.getCode());

            // Verify deleted - should return CommonResponse with code=404, HTTP 404
            String notFoundResponse = mockMvc.perform(get(BASE_URL + "/" + rule.getId()))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            CommonResponse<Void> notFoundResult = jsonMapper.readValue(notFoundResponse, voidTypeRef);
            Assertions.assertEquals(CommonResponse.CODE_NOT_FOUND, notFoundResult.getCode());

            log.info("Deleted rule via HTTP: ID={}", rule.getId());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteNonExistentRule() {
        try {
            mockMvc.perform(delete(BASE_URL + "/999999"))
                    .andExpect(status().isNotFound());
            log.info("Delete non-existent rule returns 404 as expected");
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
                Arguments.of(1, 100)
        );
    }

    @ParameterizedTest(name = "Page List: pageNum={0}, pageSize={1}")
    @MethodSource("pageListScenarios")
    void testPageListRules(Integer pageNum, Integer pageSize) {
        try {
            // Create multiple rules
            for (int i = 0; i < 5; i++) {
                CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
                rule.setCriteria("Page Test Rule " + i);
                rule.setMandatoryPass(i % 2 == 0);
                rule.setScoreContribution(new BigDecimal("0.1000"));
                rule.setScoreType(0);
                rule.setScore(new BigDecimal("10.00"));
                rulesService.create(rule);
            }

            // Page query via HTTP
            String response = mockMvc.perform(get(BASE_URL + "/page")
                            .param("pageNum", String.valueOf(pageNum))
                            .param("pageSize", String.valueOf(pageSize)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            tools.jackson.core.type.TypeReference<CommonResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CreditCardOnboardingRules>>> typeRef = 
                new tools.jackson.core.type.TypeReference<CommonResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CreditCardOnboardingRules>>>() {};
            CommonResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CreditCardOnboardingRules>> pageResponse = jsonMapper.readValue(response, typeRef);
            
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<CreditCardOnboardingRules> page = pageResponse.getData();
            Assertions.assertNotNull(page.getRecords());
            Assertions.assertNotNull(page.getTotal());
            Assertions.assertNotNull(page.getCurrent());
            Assertions.assertNotNull(page.getSize());

            int recordCount = page.getRecords().size();
            log.info("Page {} returned {} records, total: {}", 
                    pageNum, recordCount, page.getTotal());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
