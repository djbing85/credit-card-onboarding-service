package com.jasper.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Credit Card Onboarding Rules DTO
 */
@Data
public class CreditCardOnboardingRulesDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * ID (used for update)
     */
    private Integer id;

    /**
     * Evaluation criteria
     */
    @NotBlank(message = "Criteria is required")
    @Size(max = 64, message = "Criteria must not exceed 64 characters")
    private String criteria;

    /**
     * Mandatory pass
     */
    @NotNull(message = "Mandatory pass is required")
    private Boolean mandatoryPass;

    /**
     * Score contribution
     */
    private BigDecimal scoreContribution;

    /**
     * Score type: 0 boolean, 1 decimal
     */
    @NotNull(message = "Score type is required")
    private Integer scoreType;

    /**
     * Score
     */
    private BigDecimal score;
}
