package com.jasper.service.executor;

import com.jasper.entity.CreditCardOnboarding;
import com.jasper.thirdparty.RuleVerificationService;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Rule Executor
 * Responsible for executing rule verification services concurrently
 * Uses template method pattern to eliminate code duplication
 */
@Slf4j
@Component
public class RuleExecutor {
    
    @Autowired
    @Qualifier("mdcVirtualThreadExecutor")
    private Executor executor;
    
    /**
     * Execute rules concurrently (template method)
     *
     * @param onboarding the onboarding application data
     * @param ruleMap map of rule names to rule verification services
     * @param ruleType description of rule type for logging (e.g., "mandatory", "non-mandatory")
     * @return map of rule names to verification results
     */
    public Map<String, RuleVerificationResponseVO> executeRules(
            CreditCardOnboarding onboarding,
            Map<String, RuleVerificationService> ruleMap,
            String ruleType) {
        
        // If no rules, return empty map
        if (ruleMap == null || ruleMap.isEmpty()) {
            log.debug("No {} rules to execute", ruleType);
            return new ConcurrentHashMap<>();
        }

        log.debug("Executing {} {} rules concurrently", ruleMap.size(), ruleType);
        
        // Create futures for all rules
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Map<String, RuleVerificationResponseVO> results = new ConcurrentHashMap<>();

        ruleMap.forEach((ruleName, service) -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    log.debug("Executing {} rule: {}", ruleType, ruleName);
                    RuleVerificationResponseVO response = service.verify(onboarding);
                    results.put(ruleName, response);
                    log.info("Completed {} rule: {}, verified: {}", ruleType, ruleName, response.getVerified());
                } catch (Exception e) {
                    log.error("Error executing {} rule: {}", ruleType, ruleName, e);
                    results.put(ruleName, new RuleVerificationResponseVO(
                        false, 
                        java.math.BigDecimal.ZERO, 
                        ruleName, 
                        e.getMessage()
                    ));
                }
            }, executor);
            futures.add(future);
        });

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        log.debug("All {} rules completed. Total results: {}", ruleType, results.size());

        return results;
    }
}
