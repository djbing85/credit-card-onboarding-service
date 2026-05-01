package com.jasper.thirdparty.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * Compliance Check Request DTO
 */
@Data
public class ComplianceCheckRequestDTO implements Serializable {
    
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
     * Nationality
     */
    private String nationality;

    /**
     * Residential address
     */
    private String address;
}
