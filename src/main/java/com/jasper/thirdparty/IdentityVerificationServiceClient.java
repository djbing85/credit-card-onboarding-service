package com.jasper.thirdparty;

import com.jasper.thirdparty.dto.IdentityVerificationRequestDTO;
import com.jasper.thirdparty.vo.IdentityVerificationResponseVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Identity Verification Service Client
 *
 * Verify identity by connecting to government services provided by ICA
 * (Federal Authority for Identity, Citizenship, Customs and Port Security).
 */
@HttpExchange("/identity")
public interface IdentityVerificationServiceClient {
    
    /**
     * Verify identity through ICA government services
     *
     * @param request identity verification request parameters
     * @return identity verification result (verified or not verified)
     */
    @PostExchange("/verify")
    IdentityVerificationResponseVO verifyIdentity(@RequestBody IdentityVerificationRequestDTO request);
}
