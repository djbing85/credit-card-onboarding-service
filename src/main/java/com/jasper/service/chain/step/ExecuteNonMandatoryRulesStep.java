package com.jasper.service.chain.step;

import com.jasper.service.chain.VerificationContext;
import com.jasper.service.chain.VerificationStep;
import com.jasper.service.executor.RuleExecutor;
import com.jasper.thirdparty.RuleVerificationService;
import com.jasper.thirdparty.factory.CreditCardOnboardingRuleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Step 3: Execute Non-Mandatory Rules
 * Executes all non-mandatory rule verification services concurrently
 */
@Slf4j
@Component
public class ExecuteNonMandatoryRulesStep implements VerificationStep {
    
    @Autowired
    private CreditCardOnboardingRuleFactory ruleFactory;
    
    @Autowired
    private RuleExecutor ruleExecutor;
    
    @Override
    public boolean execute(VerificationContext context) {
        log.debug("Executing non-mandatory rules");
        
        // Get non-mandatory rule services
        var nonMandatoryRuleMap = ruleFactory.getAllRuleServices(false);
        
        // Execute non-mandatory rules
        var nonMandatoryResults = 
            ruleExecutor.executeRules(context.getOnboarding(), nonMandatoryRuleMap, "non-mandatory");
        
        context.setNonMandatoryResults(nonMandatoryResults);
        
        log.debug("completed: Non-mandatory rules executed");
        return true; // Continue chain
    }
    
    @Override
    public String getStepName() {
        return "ExecuteNonMandatoryRules";
    }
}
