package com.jasper.thirdparty.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * Employment Verification Response VO
 */
@Data
public class EmploymentVerificationResponseVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Employment verification result: true (Verified) or false (Not Verified)
     */
    private Boolean verified;
}
