package com.jasper.thirdparty.factory;

import com.jasper.entity.CreditCardOnboardingRules;
import com.jasper.thirdparty.RuleVerificationService;
import com.jasper.thirdparty.cache.RuleCacheManager;
import com.jasper.thirdparty.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Credit Card Onboarding Rule Factory
 * Provides rule verification services based on database configuration.
 * Uses RuleCacheManager for caching rule configurations.
 */
@Slf4j
@Component
public class CreditCardOnboardingRuleFactory {

    @Autowired
    private RuleCacheManager ruleCacheManager;

    @Autowired
    private IdentityVerificationRuleServiceImpl identityVerificationRuleService;

    @Autowired
    private ComplianceCheckRuleServiceImpl complianceCheckRuleService;

    @Autowired
    private EmploymentVerificationRuleServiceImpl employmentVerificationRuleService;

    @Autowired
    private RiskEvaluationRuleServiceImpl riskEvaluationRuleService;

    @Autowired
    private BehavioralAnalysisRuleServiceImpl behavioralAnalysisRuleService;

    private final Map<String, RuleVerificationService> allRuleServices = new HashMap<>();

    @PostConstruct
    public void init() {
        // Build service map using SERVICE_NAME constants as keys
        allRuleServices.put(IdentityVerificationRuleServiceImpl.SERVICE_NAME, identityVerificationRuleService);
        allRuleServices.put(ComplianceCheckRuleServiceImpl.SERVICE_NAME, complianceCheckRuleService);
        allRuleServices.put(EmploymentVerificationRuleServiceImpl.SERVICE_NAME, employmentVerificationRuleService);
        allRuleServices.put(RiskEvaluationRuleServiceImpl.SERVICE_NAME, riskEvaluationRuleService);
        allRuleServices.put(BehavioralAnalysisRuleServiceImpl.SERVICE_NAME, behavioralAnalysisRuleService);
        
        log.info("Rule factory initialized with {} services", allRuleServices.size());
    }

    /**
     * Get all enabled rule services
     *
     * @return map of criteria to rule verification service
     */
    public Map<String, RuleVerificationService> getAllRuleServices() {
        return new HashMap<>(allRuleServices);
    }

    /**
     * Get all enabled rule services filtered by mandatory flag
     *
     * @param mandatory if true, return only mandatory rules; if false,
     *                  return only non-mandatory rules; if null, return all rules
     * @return map of criteria to rule verification service
     */
    public Map<String, RuleVerificationService> getAllRuleServices(Boolean mandatory) {
        if (mandatory == null) {
            return getAllRuleServices();
        }

        // Get rules from cache manager
        List<CreditCardOnboardingRules> filteredRules = mandatory ? 
            ruleCacheManager.getMandatoryRules() : ruleCacheManager.getNonMandatoryRules();

        // Build filtered service map
        Map<String, RuleVerificationService> filteredServiceMap = new HashMap<>();
        for (CreditCardOnboardingRules rule : filteredRules) {
            RuleVerificationService service = allRuleServices.get(rule.getCriteria());
            if (service != null) {
                filteredServiceMap.put(rule.getCriteria(), service);
            }
        }

        return filteredServiceMap;
    }

    /**
     * Get all enabled rules from cache
     *
     * @return list of all enabled credit card onboarding rules
     */
    public List<CreditCardOnboardingRules> getAllRulesFromCache() {
        return ruleCacheManager.getAllEnabledRules();
    }
}
