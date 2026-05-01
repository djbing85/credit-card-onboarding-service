package com.jasper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Credit Card Onboarding Rules Entity
 */
@Data
@TableName("credit_card_onboarding_rules")
public class CreditCardOnboardingRules implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Status constants
     */
    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_ENABLED = 1;
    public static final int STATUS_DELETED = 9;

    /**
     * Score type constants
     */
    public static final int SCORE_TYPE_BOOLEAN = 0;
    public static final int SCORE_TYPE_DECIMAL = 1;

    /**
     * Auto-increment ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * Evaluation criteria
     */
    @TableField("criteria")
    private String criteria;

    /**
     * Mandatory pass
     */
    @TableField("mandatory_pass")
    private Boolean mandatoryPass;

    /**
     * Score contribution
     */
    @TableField("score_contribution")
    private BigDecimal scoreContribution;

    /**
     * Score type: 0 boolean, 1 decimal
     */
    @TableField("score_type")
    private Integer scoreType;

    /**
     * Score
     */
    @TableField("score")
    private BigDecimal score;

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
