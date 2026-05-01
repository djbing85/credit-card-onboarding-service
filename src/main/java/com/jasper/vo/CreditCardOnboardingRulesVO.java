package com.jasper.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Credit Card Onboarding Rules VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardOnboardingRulesVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Auto-increment ID
     */
    private Integer id;

    /**
     * Evaluation criteria
     */
    private String criteria;

    /**
     * Mandatory pass
     */
    private Boolean mandatoryPass;

    /**
     * Score contribution
     */
    private BigDecimal scoreContribution;

    /**
     * Score type: 0 boolean, 1 decimal
     */
    private Integer scoreType;

    /**
     * Score
     */
    private BigDecimal score;

    /**
     * Created time
     */
    private Long createdTime;

    /**
     * Updated time
     */
    private Long updatedTime;

    /**
     * Version
     */
    private Long version;

    /**
     * Status: 0 disabled, 1 enabled
     */
    private Integer status;

    /**
     * Operator
     */
    private String operator;
}
