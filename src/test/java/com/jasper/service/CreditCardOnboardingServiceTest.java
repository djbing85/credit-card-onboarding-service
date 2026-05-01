package com.jasper.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jasper.BaseIntegrationTest;
import com.jasper.entity.CreditCardOnboarding;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.stream.Stream;

/**
 * CreditCardOnboardingService Unit Test
 */
@Slf4j
class CreditCardOnboardingServiceTest extends BaseIntegrationTest {

    @Autowired
    private CreditCardOnboardingService onboardingService;

    /**
     * Test create onboarding application
     */
    @Test
    void testCreate() {
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber("784-SVC-TEST-001");
        onboarding.setName("Test User");
        onboarding.setMobileNumber("+971501234567");
        onboarding.setNationality("UAE");
        onboarding.setAddress("Dubai Marina");
        onboarding.setIncome(new BigDecimal("240000"));
        onboarding.setEmploymentDetails("Employed at Tech Corp");
        onboarding.setRequestedCreditLimit(new BigDecimal("50000"));

        CreditCardOnboarding result = onboardingService.create(onboarding);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals("784-SVC-TEST-001", result.getEmiratesIdNumber());
        Assertions.assertEquals(CreditCardOnboarding.STATUS_ENABLED, result.getStatus());
        Assertions.assertNotNull(result.getCreatedTime());
        Assertions.assertNotNull(result.getUpdatedTime());
        Assertions.assertEquals(0L, result.getVersion());
        Assertions.assertEquals("system", result.getOperator());

        log.info("Created onboarding with ID: {}", result.getId());
    }

    /**
     * Test get by ID
     */
    @Test
    void testGetById() {
        // Create first
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber("784-SVC-TEST-002");
        onboarding.setName("Get Test User");
        onboardingService.create(onboarding);

        // Get by ID
        CreditCardOnboarding found = onboardingService.getById(onboarding.getId());

        Assertions.assertNotNull(found);
        Assertions.assertEquals("784-SVC-TEST-002", found.getEmiratesIdNumber());
        Assertions.assertEquals("Get Test User", found.getName());

        log.info("Retrieved onboarding with ID: {}", found.getId());
    }

    /**
     * Test get by non-existent ID
     */
    @Test
    void testGetByIdNotFound() {
        CreditCardOnboarding found = onboardingService.getById(999999L);
        Assertions.assertNull(found);
        log.info("Non-existent ID returns null as expected");
    }

    /**
     * Test page list
     */
    @Test
    void testPageList() {
        // Create multiple records
        for (int i = 0; i < 5; i++) {
            CreditCardOnboarding onboarding = new CreditCardOnboarding();
            onboarding.setEmiratesIdNumber("784-SVC-PAGE-" + String.format("%03d", i));
            onboarding.setName("Page Test User " + i);
            onboardingService.create(onboarding);
        }

        // Page query
        Page<CreditCardOnboarding> page = onboardingService.pageList(1, 3);

        Assertions.assertNotNull(page);
        Assertions.assertEquals(3, page.getSize());
        Assertions.assertFalse(page.getRecords().isEmpty());
        
        // Verify we got at least the records we just created
        long pageTestRecords = page.getRecords().stream()
                .filter(r -> r.getEmiratesIdNumber().startsWith("784-SVC-PAGE-"))
                .count();
        Assertions.assertTrue(pageTestRecords > 0, "Should have at least some page test records");

        // Verify order (desc by ID)
        Long previousId = Long.MAX_VALUE;
        for (CreditCardOnboarding record : page.getRecords()) {
            Assertions.assertTrue(record.getId() <= previousId);
            previousId = record.getId();
        }

        log.info("Page query returned {} records, total: {}", page.getRecords().size(), page.getTotal());
    }

    /**
     * Test update onboarding
     */
    @Test
    void testUpdate() {
        // Create first
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber("784-SVC-TEST-003");
        onboarding.setName("Original Name");
        onboardingService.create(onboarding);

        Long id = onboarding.getId();
        
        // Verify initial state
        CreditCardOnboarding beforeUpdate = onboardingService.getById(id);
        Assertions.assertNotNull(beforeUpdate);
        Assertions.assertEquals("Original Name", beforeUpdate.getName());
        Assertions.assertEquals(0L, beforeUpdate.getVersion());
        Assertions.assertNotNull(beforeUpdate.getCreatedTime());
        Assertions.assertNotNull(beforeUpdate.getStatus());
        Assertions.assertNotNull(beforeUpdate.getOperator());

        // Update
        CreditCardOnboarding updateRequest = new CreditCardOnboarding();
        updateRequest.setId(id);
        updateRequest.setName("Updated Name");
        updateRequest.setIncome(new BigDecimal("300000"));
        CreditCardOnboarding updated = onboardingService.update(updateRequest);

        // Verify returned object has all fields populated
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(id, updated.getId());
        Assertions.assertEquals("Updated Name", updated.getName());
        Assertions.assertEquals(0, new BigDecimal("300000").compareTo(updated.getIncome()), "Income should match");
        Assertions.assertEquals(1L, updated.getVersion());
        
        // Verify preserved fields are not null
        Assertions.assertNotNull(updated.getCreatedTime(), "createdTime should not be null");
        Assertions.assertNotNull(updated.getStatus(), "status should not be null");
        Assertions.assertNotNull(updated.getOperator(), "operator should not be null");
        Assertions.assertNotNull(updated.getUpdatedTime(), "updatedTime should not be null");
        
        // Verify preserved fields maintain original values
        Assertions.assertEquals(beforeUpdate.getCreatedTime(), updated.getCreatedTime());
        Assertions.assertEquals(beforeUpdate.getStatus(), updated.getStatus());
        Assertions.assertEquals(beforeUpdate.getOperator(), updated.getOperator());

        log.info("Updated onboarding version: {}, all fields present", updated.getVersion());
    }

    /**
     * Test update non-existent onboarding
     */
    @Test
    void testUpdateNonExistent() {
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setId(999999L);
        onboarding.setName("Non-existent");
        
        // Should throw RuntimeException
        RuntimeException exception = Assertions.assertThrows(
            RuntimeException.class,
            () -> onboardingService.update(onboarding)
        );
        Assertions.assertTrue(exception.getMessage().contains("Onboarding not found with id: 999999"));
        log.info("Update non-existent onboarding throws exception as expected: {}", exception.getMessage());
    }

    /**
     * Test delete onboarding
     */
    @Test
    void testDelete() {
        // Create first
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber("784-SVC-TEST-004");
        onboarding.setName("Delete Test User");
        onboardingService.create(onboarding);

        Long id = onboarding.getId();

        // Delete
        boolean deleted = onboardingService.delete(id);
        Assertions.assertTrue(deleted);

        // Verify deleted (should return null or have status=9)
        CreditCardOnboarding found = onboardingService.getById(id);
        Assertions.assertNull(found);

        log.info("Deleted onboarding with ID: {}", id);
    }

    /**
     * Test delete non-existent record
     */
    @Test
    void testDeleteNotFound() {
        boolean deleted = onboardingService.delete(999999L);
        Assertions.assertFalse(deleted);
        log.info("Delete non-existent ID returns false as expected");
    }

    /**
     * Test update verification result
     */
    @Test
    void testUpdateVerificationResult() {
        // Create first
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber("784-SVC-TEST-005");
        onboarding.setName("Verify Test User");
        onboardingService.create(onboarding);

        // Update verification result
        onboardingService.updateVerificationResult(
                onboarding.getId(),
                "true",
                "0.8500",
                "{\"identityCheck\": true, \"riskScore\": 0.85}"
        );

        // Verify
        CreditCardOnboarding updated = onboardingService.getById(onboarding.getId());
        Assertions.assertNotNull(updated);
        Assertions.assertEquals("true", updated.getVerifiedResult());
        Assertions.assertEquals("0.8500", updated.getVerifiedScore());
        Assertions.assertNotNull(updated.getVerifiedDetail());
        Assertions.assertNotNull(updated.getVerifiedTime());

        log.info("Updated verification result: {}", updated.getVerifiedResult());
    }

    /**
     * Test update verification result for non-existent ID
     */
    @Test
    void testUpdateVerificationResultNotFound() {
        // Should not throw exception
        onboardingService.updateVerificationResult(
                999999L,
                "false",
                "0.0000",
                "{}"
        );
        log.info("Update verification for non-existent ID completed without error");
    }

    /**
     * Test update all verification fields
     */
    @Test
    void testUpdateAllVerificationFields() {
        // Create first
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber("784-SVC-TEST-006");
        onboarding.setName("Full Verify Test User");
        onboardingService.create(onboarding);

        // Update all verification fields
        String detail = "{\"mandatoryRules\":{\"Identity\":{\"verified\":true}},\"nonMandatoryRules\":{\"Risk\":{\"verified\":true}}}";
        onboardingService.updateAllVerificationFields(
                onboarding.getId(),
                "true",
                "0.9500",
                detail
        );

        // Verify
        CreditCardOnboarding updated = onboardingService.getById(onboarding.getId());
        Assertions.assertNotNull(updated);
        Assertions.assertEquals("true", updated.getVerifiedResult());
        Assertions.assertEquals("0.9500", updated.getVerifiedScore());
        Assertions.assertEquals(detail, updated.getVerifiedDetail());
        Assertions.assertNotNull(updated.getVerifiedTime());
        Assertions.assertNotNull(updated.getUpdatedTime());
        Assertions.assertEquals(1L, updated.getVersion());

        log.info("Updated all verification fields successfully");
    }

    /**
     * Test update all verification fields for non-existent ID
     */
    @Test
    void testUpdateAllVerificationFieldsNotFound() {
        // Should not throw exception
        onboardingService.updateAllVerificationFields(
                999999L,
                "rejected",
                "0.0000",
                "{}"
        );
        log.info("Update all verification fields for non-existent ID completed without error");
    }

    /**
     * Test edge case: create with minimum income
     */
    @Test
    void testCreateWithMinimumIncome() {
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber("784-SVC-EDGE-001");
        onboarding.setName("Min Income User");
        onboarding.setIncome(new BigDecimal("0"));
        onboarding.setRequestedCreditLimit(new BigDecimal("0"));
        onboardingService.create(onboarding);

        Assertions.assertNotNull(onboarding.getId());
        Assertions.assertEquals(0, new BigDecimal("0").compareTo(onboarding.getIncome()), "Income should be zero");
        log.info("Created onboarding with zero income");
    }

    /**
     * Test edge case: create with high credit limit
     */
    @Test
    void testCreateWithHighCreditLimit() {
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber("784-SVC-EDGE-002");
        onboarding.setName("High Limit User");
        onboarding.setIncome(new BigDecimal("1000000"));
        onboarding.setRequestedCreditLimit(new BigDecimal("500000"));
        onboardingService.create(onboarding);

        Assertions.assertNotNull(onboarding.getId());
        Assertions.assertEquals(0, new BigDecimal("500000").compareTo(onboarding.getRequestedCreditLimit()), "Credit limit should match");
        log.info("Created onboarding with high credit limit");
    }
}
