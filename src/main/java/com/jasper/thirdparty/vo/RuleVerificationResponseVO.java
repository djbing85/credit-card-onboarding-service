package com.jasper.thirdparty.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Rule Verification Response VO
 * Unified response object for all rule verification services
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleVerificationResponseVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Verification result: true (passed) or false (failed)
     */
    private Boolean verified;

    /**
     * Risk score: 0.0000 ~ 1.0000 (0% - 100%)
     * For boolean type rules, verified=true means 1.0000, verified=false means 0.0000
     */
    private BigDecimal riskScore;

    private String ruleName;

    private String errorMessage;
}
