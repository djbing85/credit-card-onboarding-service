package com.jasper.thirdparty.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * Employment Verification Request DTO
 */
@Data
public class EmploymentVerificationRequestDTO implements Serializable {
    
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
     * Employment details
     */
    private String employmentDetails;

    /**
     * Annual income
     */
    private java.math.BigDecimal income;
}
