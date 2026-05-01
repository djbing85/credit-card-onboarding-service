package com.jasper.thirdparty.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * Identity Verification Response VO
 */
@Data
public class IdentityVerificationResponseVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Identity verification result: true (Verified) or false (Not Verified)
     */
    private Boolean verified;
}
