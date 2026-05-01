package com.jasper.thirdparty.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * Behavioral Analysis Request DTO
 */
@Data
public class BehavioralAnalysisRequestDTO implements Serializable {
    
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
     * Bank statement file path
     */
    private String bankStatement;

    /**
     * Annual income
     */
    private java.math.BigDecimal income;
}
