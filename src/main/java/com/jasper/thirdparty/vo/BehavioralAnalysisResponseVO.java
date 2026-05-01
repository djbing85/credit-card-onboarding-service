package com.jasper.thirdparty.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Behavioral Analysis Response VO
 */
@Data
public class BehavioralAnalysisResponseVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Behavioral analysis score: 0.0000 ~ 1.0000 (0% - 100%)
     */
    private BigDecimal behavioralScore;
}
