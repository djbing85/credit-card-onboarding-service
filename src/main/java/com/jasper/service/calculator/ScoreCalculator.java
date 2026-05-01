package com.jasper.service.calculator;

import com.jasper.entity.CreditCardOnboardingRules;
import com.jasper.service.strategy.ScoreStrategyManager;
import com.jasper.thirdparty.factory.CreditCardOnboardingRuleFactory;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Score Calculator
 * Responsible for calculating total verification score from rule results
 */
@Slf4j
@Component
public class ScoreCalculator {
    
    @Autowired
    private CreditCardOnboardingRuleFactory ruleFactory;
    
    @Autowired
    private ScoreStrategyManager scoreStrategyManager;

    @Autowired
    private JsonMapper jsonMapper;
    
    /**
     * Calculate total score from all rule results
     *
     * @param mandatoryResults mandatory rule verification results
     * @param nonMandatoryResults non-mandatory rule verification results
     * @return calculated total score (0.0 to 1.0)
     * @throws RuntimeException if rule configuration mismatch or invalid score
     */
    public BigDecimal calculateTotalScore(Map<String, RuleVerificationResponseVO> mandatoryResults,
                                         Map<String, RuleVerificationResponseVO> nonMandatoryResults) {
        // Load all enabled rules from cache
        List<CreditCardOnboardingRules> allRules = loadAllEnabledRules();
        
        if (allRules == null || allRules.isEmpty()) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        
        // Combine all results
        Map<String, RuleVerificationResponseVO> allResults = new HashMap<>();
        allResults.putAll(mandatoryResults);
        allResults.putAll(nonMandatoryResults);
        
        BigDecimal totalScore = BigDecimal.ZERO;

        // Iterate through all rules and calculate score based on strategy
        for (CreditCardOnboardingRules rule : allRules) {
            String criteria = rule.getCriteria();
            RuleVerificationResponseVO result = allResults.get(criteria);
            
            // If rule exists in config but no result, throw exception
            if (result == null) {
                log.warn("Rule configuration exists but no verification result: {}", jsonMapper.writeValueAsString(rule));
                continue;
            }
            
            Integer scoreType = rule.getScoreType();
            
            try {
                // Use strategy pattern to calculate score contribution
                var strategy = scoreStrategyManager.getStrategy(scoreType);
                BigDecimal contribution = strategy.calculate(rule, result);
                totalScore = totalScore.add(contribution);
            } catch (IllegalArgumentException e) {
                log.error("Invalid score type: {}", scoreType);
                throw new RuntimeException("Invalid score type: " + scoreType);
            }
        }

        // Validate totalScore range
        if (totalScore.compareTo(BigDecimal.ONE) > 0 || totalScore.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Invalid totalScore: {}", totalScore);
            throw new RuntimeException("Invalid totalScore: " + totalScore);
        }

        return totalScore.setScale(4, RoundingMode.HALF_UP);
    }
    
    /**
     * Load all enabled rules from cache
     */
    private List<CreditCardOnboardingRules> loadAllEnabledRules() {
        try {
            return ruleFactory.getAllRulesFromCache();
        } catch (Exception e) {
            log.error("Error loading rules from cache", e);
            return java.util.Collections.emptyList();
        }
    }
}
