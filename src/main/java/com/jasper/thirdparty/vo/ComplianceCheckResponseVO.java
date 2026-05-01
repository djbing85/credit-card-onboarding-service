package com.jasper.thirdparty.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * Compliance Check Response VO
 */
@Data
public class ComplianceCheckResponseVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Compliance check result: true (Passed) or false (Failed)
     */
    private Boolean passed;
}
