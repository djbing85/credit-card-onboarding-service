package com.jasper.thirdparty.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * Risk Evaluation Request DTO
 */
@Data
public class RiskEvaluationRequestDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Emirates ID number
     */
    private String emiratesIdNumber;

    /**
     * Applicant full name
     */
    private String name;

    /**
     * Annual income
     */
    private java.math.BigDecimal income;

    /**
     * Requested credit limit
     */
    private java.math.BigDecimal requestedCreditLimit;
}
