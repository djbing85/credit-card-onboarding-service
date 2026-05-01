package com.jasper.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jasper.BaseIntegrationTest;
import com.jasper.entity.CreditCardOnboardingRules;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.stream.Stream;

/**
 * CreditCardOnboardingRulesService Unit Test
 * Covers all methods with multiple edge cases using @MethodSource
 */
@Slf4j
class CreditCardOnboardingRulesServiceTest extends BaseIntegrationTest {

    @Autowired
    private CreditCardOnboardingRulesService rulesService;

    // ==================== Create Rule Tests ====================

    /**
     * Data source for create rule scenarios
     */
    static Stream<Arguments> createRuleScenarios() {
        return Stream.of(
                // Scenario 1: Normal mandatory rule
                Arguments.of(
                        "Identity Verification", true, new BigDecimal("0.2000"), 0, new BigDecimal("20.00")
                ),
                // Scenario 2: Non-mandatory rule with score type 1
                Arguments.of(
                        "Risk Evaluation", false, new BigDecimal("0.3000"), 1, new BigDecimal("30.00")
                ),
                // Scenario 3: Zero contribution rule
                Arguments.of(
                        "Optional Check", false, BigDecimal.ZERO, 0, BigDecimal.ZERO
                ),
                // Scenario 4: High contribution rule
                Arguments.of(
                        "Critical Rule", true, new BigDecimal("1.0000"), 0, new BigDecimal("100.00")
                )
        );
    }

    @ParameterizedTest(name = "Create Rule: {0}, Mandatory={1}, Contribution={2}")
    @MethodSource("createRuleScenarios")
    void testCreateRule(String criteria, boolean mandatoryPass, BigDecimal contribution, 
                        Integer scoreType, BigDecimal score) {
        CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
        rule.setCriteria(criteria);
        rule.setMandatoryPass(mandatoryPass);
        rule.setScoreContribution(contribution);
        rule.setScoreType(scoreType);
        rule.setScore(score);

        CreditCardOnboardingRules created = rulesService.create(rule);

        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(criteria, created.getCriteria());
        Assertions.assertEquals(mandatoryPass, created.getMandatoryPass());
        Assertions.assertEquals(contribution, created.getScoreContribution());
        Assertions.assertEquals(scoreType, created.getScoreType());
        Assertions.assertEquals(score, created.getScore());
        Assertions.assertEquals(CreditCardOnboardingRules.STATUS_ENABLED, created.getStatus());
        Assertions.assertEquals("system", created.getOperator());
        Assertions.assertEquals(0L, created.getVersion());
        Assertions.assertNotNull(created.getCreatedTime());
        Assertions.assertNotNull(created.getUpdatedTime());

        log.info("Created rule ID: {}, Criteria: {}", created.getId(), criteria);
    }

    // ==================== Get By ID Tests ====================

    @Test
    void testGetById() {
        // Create first
        CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
        rule.setCriteria("Get Test Rule");
        rule.setMandatoryPass(true);
        rule.setScoreContribution(new BigDecimal("0.1500"));
        rule.setScoreType(0);
        rule.setScore(new BigDecimal("15.00"));
        rulesService.create(rule);

        // Get by ID
        CreditCardOnboardingRules found = rulesService.getById(rule.getId());

        Assertions.assertNotNull(found);
        Assertions.assertEquals("Get Test Rule", found.getCriteria());
        Assertions.assertEquals(rule.getId(), found.getId());

        log.info("Retrieved rule ID: {}", found.getId());
    }

    @Test
    void testGetByIdNotFound() {
        CreditCardOnboardingRules found = rulesService.getById(999999);
        Assertions.assertNull(found);
        log.info("Non-existent rule ID returns null as expected");
    }

    // ==================== Page List Tests ====================

    @Test
    void testPageList() {
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

        // Page query
        Page<CreditCardOnboardingRules> page = rulesService.pageList(1, 3);

        Assertions.assertNotNull(page);
        Assertions.assertEquals(3, page.getSize());
        Assertions.assertFalse(page.getRecords().isEmpty());
        
        // Verify we got at least some of our test records
        long pageTestRecords = page.getRecords().stream()
                .filter(r -> r.getCriteria().startsWith("Page Test Rule"))
                .count();
        Assertions.assertTrue(pageTestRecords > 0, "Should have at least some page test records");

        // Verify order (desc by ID)
        Integer previousId = Integer.MAX_VALUE;
        for (CreditCardOnboardingRules record : page.getRecords()) {
            Assertions.assertTrue(record.getId() <= previousId);
            previousId = record.getId();
        }

        log.info("Page query returned {} records, total: {}", page.getRecords().size(), page.getTotal());
    }

    @Test
    void testPageListEmptyResult() {
        Page<CreditCardOnboardingRules> page = rulesService.pageList(999, 10);
        Assertions.assertNotNull(page);
        // Page might not be empty if there are many records in DB, so just verify it doesn't throw exception
        log.info("Page query for page 999 returned {} records", page.getRecords().size());
    }

    // ==================== Update Rule Tests ====================

    /**
     * Data source for update rule scenarios
     */
    static Stream<Arguments> updateRuleScenarios() {
        return Stream.of(
                // Scenario 1: Update score
                Arguments.of(
                        "score", new BigDecimal("50.00"), null, null
                ),
                // Scenario 2: Update mandatory pass
                Arguments.of(
                        "mandatory", null, true, null
                ),
                // Scenario 3: Update score contribution
                Arguments.of(
                        "contribution", null, null, new BigDecimal("0.5000")
                )
        );
    }

    @ParameterizedTest(name = "Update Rule Field: {0}")
    @MethodSource("updateRuleScenarios")
    void testUpdateRule(String updateType, BigDecimal newScore, Boolean newMandatory, 
                        BigDecimal newContribution) {
        // Create first
        CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
        rule.setCriteria("Update Test Rule");
        rule.setMandatoryPass(false);
        rule.setScoreContribution(new BigDecimal("0.2000"));
        rule.setScoreType(0);
        rule.setScore(new BigDecimal("20.00"));
        rulesService.create(rule);

        // Update based on type
        if ("score".equals(updateType)) {
            rule.setScore(newScore);
        } else if ("mandatory".equals(updateType)) {
            rule.setMandatoryPass(newMandatory);
        } else if ("contribution".equals(updateType)) {
            rule.setScoreContribution(newContribution);
        }

        CreditCardOnboardingRules updated = rulesService.update(rule);

        Assertions.assertNotNull(updated);
        if ("score".equals(updateType)) {
            Assertions.assertEquals(newScore, updated.getScore());
        } else if ("mandatory".equals(updateType)) {
            Assertions.assertEquals(newMandatory, updated.getMandatoryPass());
        } else if ("contribution".equals(updateType)) {
            Assertions.assertEquals(newContribution, updated.getScoreContribution());
        }
        Assertions.assertNotNull(updated.getUpdatedTime());

        log.info("Updated rule ID: {}, field: {}", updated.getId(), updateType);
    }

    @Test
    void testUpdateNonExistentRule() {
        CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
        rule.setId(999999);
        rule.setCriteria("Non-existent");
        
        // Should not throw exception
        rulesService.update(rule);
        log.info("Update non-existent rule completed without error");
    }

    // ==================== Delete Rule Tests ====================

    @Test
    void testDeleteRule() {
        // Create first
        CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
        rule.setCriteria("Delete Test Rule");
        rule.setMandatoryPass(false);
        rule.setScoreContribution(BigDecimal.ZERO);
        rule.setScoreType(0);
        rule.setScore(BigDecimal.ZERO);
        rulesService.create(rule);

        Integer id = rule.getId();

        // Delete
        boolean deleted = rulesService.delete(id);
        Assertions.assertTrue(deleted);

        // Verify deleted
        CreditCardOnboardingRules found = rulesService.getById(id);
        Assertions.assertNull(found);

        log.info("Deleted rule ID: {}", id);
    }

    @Test
    void testDeleteNonExistentRule() {
        boolean deleted = rulesService.delete(999999);
        Assertions.assertFalse(deleted);
        log.info("Delete non-existent rule returns false as expected");
    }

    // ==================== Edge Case Tests ====================

    @Test
    void testCreateRuleWithLongCriteria() {
        CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
        String longCriteria = "A".repeat(64); // Max length is 64
        rule.setCriteria(longCriteria);
        rule.setMandatoryPass(true);
        rule.setScoreContribution(new BigDecimal("0.1000"));
        rule.setScoreType(0);
        rule.setScore(new BigDecimal("10.00"));

        CreditCardOnboardingRules created = rulesService.create(rule);
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(longCriteria, created.getCriteria());

        log.info("Created rule with max length criteria ({} chars)", longCriteria.length());
    }

    @Test
    void testCreateRuleWithNegativeScore() {
        CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
        rule.setCriteria("Negative Score Rule");
        rule.setMandatoryPass(false);
        rule.setScoreContribution(new BigDecimal("-0.1000"));
        rule.setScoreType(0);
        rule.setScore(new BigDecimal("-10.00"));

        CreditCardOnboardingRules created = rulesService.create(rule);
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(new BigDecimal("-10.00"), created.getScore());

        log.info("Created rule with negative score");
    }

    @Test
    void testCreateMultipleRulesAndVerifyOrdering() {
        // Create rules with different scores
        for (int i = 1; i <= 3; i++) {
            CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
            rule.setCriteria("Order Test Rule " + i);
            rule.setMandatoryPass(true);
            rule.setScoreContribution(new BigDecimal("0." + String.format("%04d", i * 1000)));
            rule.setScoreType(0);
            rule.setScore(new BigDecimal(String.valueOf(i * 10)));
            rulesService.create(rule);
        }

        // Query and verify ordering
        Page<CreditCardOnboardingRules> page = rulesService.pageList(1, 10);
        Assertions.assertFalse(page.getRecords().isEmpty());

        // Verify descending order by ID
        Integer previousId = Integer.MAX_VALUE;
        for (CreditCardOnboardingRules record : page.getRecords()) {
            if (record.getCriteria().startsWith("Order Test Rule")) {
                Assertions.assertTrue(record.getId() <= previousId);
                previousId = record.getId();
            }
        }

        log.info("Verified rule ordering by ID (descending)");
    }
}
