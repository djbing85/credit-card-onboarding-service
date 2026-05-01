package com.jasper.thirdparty.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

/**
 * Identity Verification Request DTO
 */
@Data
public class IdentityVerificationRequestDTO implements Serializable {
    
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
     * Mobile phone number
     */
    @TableField("mobile_number")
    private String mobileNumber;

    /**
     * Nationality
     */
    private String nationality;

    /**
     * Residential address
     */
    @TableField("address")
    private String address;
}
