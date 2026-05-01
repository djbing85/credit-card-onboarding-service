package com.jasper.service.builder;

import tools.jackson.databind.json.JsonMapper;
import com.jasper.thirdparty.vo.RuleVerificationResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Verification Detail Builder
 * Responsible for building verification detail JSON from rule results
 */
@Slf4j
@Component
public class VerificationDetailBuilder {
    
    @Autowired
    private JsonMapper jsonMapper;
    
    /**
     * Build verification detail JSON with all rule results
     *
     * @param mandatoryResults mandatory rule verification results
     * @param nonMandatoryResults non-mandatory rule verification results
     * @return JSON string containing all rule verification details
     */
    public String buildVerificationDetailJson(Map<String, RuleVerificationResponseVO> mandatoryResults,
                                             Map<String, RuleVerificationResponseVO> nonMandatoryResults) {
        try {
            List<Map<String, Object>> detailList = new ArrayList<>();

            // Add mandatory results
            if (mandatoryResults != null) {
                for (Map.Entry<String, RuleVerificationResponseVO> entry : mandatoryResults.entrySet()) {
                    detailList.add(buildRuleResultMap(entry.getKey(), entry.getValue()));
                }
            }

            // Add non-mandatory results
            if (nonMandatoryResults != null) {
                for (Map.Entry<String, RuleVerificationResponseVO> entry : nonMandatoryResults.entrySet()) {
                    detailList.add(buildRuleResultMap(entry.getKey(), entry.getValue()));
                }
            }

            return jsonMapper.writeValueAsString(detailList);
        } catch (Exception e) {
            log.error("Error building verification detail JSON", e);
            return "[]";
        }
    }
    
    /**
     * Convert single rule result to map
     */
    private Map<String, Object> buildRuleResultMap(String ruleName, RuleVerificationResponseVO result) {
        Map<String, Object> ruleResult = new LinkedHashMap<>();
        ruleResult.put("ruleName", ruleName);
        ruleResult.put("verified", result.getVerified());
        ruleResult.put("riskScore", result.getRiskScore());
        ruleResult.put("errorMessage", result.getErrorMessage());
        return ruleResult;
    }
}
