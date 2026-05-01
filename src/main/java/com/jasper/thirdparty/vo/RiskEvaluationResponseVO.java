package com.jasper.thirdparty.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Risk Evaluation Response VO
 */
@Data
public class RiskEvaluationResponseVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Risk score: 0.0000 ~ 1.0000 (0% - 100%)
     */
    private BigDecimal riskScore;
}
