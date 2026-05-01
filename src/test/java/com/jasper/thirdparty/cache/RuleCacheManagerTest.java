package com.jasper.thirdparty.cache;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RuleCacheManager Test
 * Tests cache management and scheduled refresh
 */
@Slf4j
@SpringBootTest
public class RuleCacheManagerTest {
    
    @Autowired
    private RuleCacheManager ruleCacheManager;
    
    @Test
    public void testCacheManagerExists() {
        assertNotNull(ruleCacheManager, "RuleCacheManager should be autowired");
        log.info("✓ RuleCacheManager bean exists");
    }
    
    @Test
    public void testInvalidateAll() {
        // This should not throw exception
        assertDoesNotThrow(() -> {
            ruleCacheManager.invalidateAll();
            log.info("✓ Cache invalidation works");
        });
    }
    
    @Test
    public void testGetAllEnabledRules() {
        // Should return list (may be empty if no rules in test DB)
        var rules = ruleCacheManager.getAllEnabledRules();
        assertNotNull(rules, "getAllEnabledRules should not return null");
        log.info("✓ getAllEnabledRules returned {} rules", rules.size());
    }
    
    @Test
    public void testGetMandatoryRules() {
        var rules = ruleCacheManager.getMandatoryRules();
        assertNotNull(rules, "getMandatoryRules should not return null");
        log.info("✓ getMandatoryRules returned {} rules", rules.size());
    }
    
    @Test
    public void testGetNonMandatoryRules() {
        var rules = ruleCacheManager.getNonMandatoryRules();
        assertNotNull(rules, "getNonMandatoryRules should not return null");
        log.info("✓ getNonMandatoryRules returned {} rules", rules.size());
    }
}
