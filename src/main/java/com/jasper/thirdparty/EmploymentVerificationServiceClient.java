package com.jasper.thirdparty;

import com.jasper.thirdparty.dto.EmploymentVerificationRequestDTO;
import com.jasper.thirdparty.vo.EmploymentVerificationResponseVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Employment Verification Service Client
 *
 * Verify the employment details of credit card applicants to ensure they have
 * a stable and reliable source of income.
 */
@HttpExchange("/employment")
public interface EmploymentVerificationServiceClient {
    
    /**
     * Verify employment details
     *
     * @param request employment verification request parameters
     * @return employment verification result (verified or not verified)
     */
    @PostExchange("/verify")
    EmploymentVerificationResponseVO verifyEmployment(@RequestBody EmploymentVerificationRequestDTO request);
}
