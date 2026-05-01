package com.jasper.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Credit Card Onboarding VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardOnboardingVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Auto-increment ID
     */
    private Long id;

    /**
     * Emirates ID number
     */
    private String emiratesIdNumber;

    /**
     * Applicant full name
     */
    private String name;

    /**
     * Mobile phone number
     */
    private String mobileNumber;

    /**
     * Nationality
     */
    private String nationality;

    /**
     * Residential address
     */
    private String address;

    /**
     * Annual income
     */
    private BigDecimal income;

    /**
     * Employment details
     */
    private String employmentDetails;

    /**
     * Requested credit limit
     */
    private BigDecimal requestedCreditLimit;

    /**
     * Bank statement file path
     */
    private String bankStatement;

    /**
     * Verified result: true/false
     */
    private String verifiedResult;

    /**
     * Verified score: 0.0000 ~ 1.0000
     */
    private String verifiedScore;

    /**
     * Verified detail
     */
    private String verifiedDetail;

    /**
     * Verified time
     */
    private Long verifiedTime;

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
