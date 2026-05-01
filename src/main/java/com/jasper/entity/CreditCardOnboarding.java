package com.jasper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Credit Card Onboarding Entity
 */
@Data
@TableName("credit_card_onboarding")
public class CreditCardOnboarding implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Status constants
     */
    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_ENABLED = 1;
    public static final int STATUS_DELETED = 9;

    /**
     * Verification result constants
     */
    public static final String VERIFIED_RESULT_PENDING = "PENDING";
    public static final String VERIFIED_RESULT_REJECTED = "REJECTED";
    public static final String VERIFIED_RESULT_ERROR = "ERROR";
    public static final String VERIFIED_RESULT_AUTO_ISSUE = "AUTO_ISSUE";
    public static final String VERIFIED_RESULT_MANUAL_REVIEW_LIMIT = "MANUAL_REVIEW_LIMIT";
    public static final String VERIFIED_RESULT_MANUAL_REVIEW = "MANUAL_REVIEW";

    /**
     * Auto-increment ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Emirates ID number
     */
    @TableField("emirates_id_number")
    private String emiratesIdNumber;

    /**
     * Applicant full name
     */
    @TableField("name")
    private String name;

    /**
     * Mobile phone number
     */
    @TableField("mobile_number")
    private String mobileNumber;

    /**
     * Nationality
     */
    @TableField("nationality")
    private String nationality;

    /**
     * Residential address
     */
    @TableField("address")
    private String address;

    /**
     * Annual income
     */
    @TableField("income")
    private BigDecimal income;

    /**
     * Employment details
     */
    @TableField("employment_details")
    private String employmentDetails;

    /**
     * Requested credit limit
     */
    @TableField("requested_credit_limit")
    private BigDecimal requestedCreditLimit;

    /**
     * Bank statement file path
     * TODO file upload
     */
    @TableField("bank_statement")
    private String bankStatement;

    /**
     * Verified result: PENDING/PASS/REJECTED/ERROR
     */
    @TableField("verified_result")
    private String verifiedResult;

    /**
     * Verified score: 0.0000 ~ 1.0000
     */
    @TableField("verified_score")
    private String verifiedScore;

    /**
     * Verified detail
     */
    @TableField("verified_detail")
    private String verifiedDetail;

    /**
     * Verified time
     */
    @TableField("verified_time")
    private Long verifiedTime;

    /**
     * Created time
     */
    @TableField("created_time")
    private Long createdTime;

    /**
     * Updated time
     */
    @TableField("updated_time")
    private Long updatedTime;

    /**
     * Version
     */
    @TableField("version")
    private Long version;

    /**
     * Status: 0 disabled, 1 enabled, 9 deleted
     */
    @TableField("status")
    private Integer status;

    /**
     * Operator
     */
    @TableField("operator")
    private String operator;
}
