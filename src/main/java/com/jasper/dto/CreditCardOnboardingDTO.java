package com.jasper.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Credit Card Onboarding DTO
 */
@Data
public class CreditCardOnboardingDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * ID (used for update)
     */
    private Long id;

    /**
     * Emirates ID number
     */
    @NotBlank(message = "Emirates ID number is required")
    @Size(max = 64, message = "Emirates ID number must not exceed 64 characters")
    private String emiratesIdNumber;

    /**
     * Applicant full name
     */
    @NotBlank(message = "Applicant name is required")
    @Size(max = 128, message = "Applicant name must not exceed 128 characters")
    private String name;

    /**
     * Mobile phone number
     */
    @NotBlank(message = "Mobile number is required")
    @Size(max = 32, message = "Mobile number must not exceed 32 characters")
    private String mobileNumber;

    /**
     * Nationality
     */
    @NotBlank(message = "Nationality is required")
    @Size(max = 64, message = "Nationality must not exceed 64 characters")
    private String nationality;

    /**
     * Residential address
     */
    @NotBlank(message = "Residential address is required")
    @Size(max = 512, message = "Residential address must not exceed 512 characters")
    private String address;

    /**
     * Annual income
     */
    @NotNull(message = "Annual income is required")
    @DecimalMin(value = "0", message = "Annual income must be non-negative")
    private BigDecimal income;

    /**
     * Employment details
     */
    @NotBlank(message = "Employment details is required")
    @Size(max = 512, message = "Employment details must not exceed 512 characters")
    private String employmentDetails;

    /**
     * Requested credit limit
     */
    @NotNull(message = "Requested credit limit is required")
    @DecimalMin(value = "0", message = "Requested credit limit must be non-negative")
    private BigDecimal requestedCreditLimit;

    /**
     * Bank statement file path
     * TODO validate the file path format/ existence
     */
    @Size(max = 256, message = "Bank statement file path must not exceed 256 characters")
    private String bankStatement;

}
