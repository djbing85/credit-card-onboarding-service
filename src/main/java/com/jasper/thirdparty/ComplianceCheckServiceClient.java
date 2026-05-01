package com.jasper.thirdparty;

import com.jasper.thirdparty.dto.ComplianceCheckRequestDTO;
import com.jasper.thirdparty.vo.ComplianceCheckResponseVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Compliance Check Service Client
 *
 * Verify that applications comply with local and international financial regulations,
 * including anti-money laundering (AML) and know your customer (KYC) standards.
 */
@HttpExchange("/compliance")
public interface ComplianceCheckServiceClient {
    
    /**
     * Perform compliance check (blacklist check, AML, KYC)
     *
     * @param request compliance check request parameters
     * @return compliance check result (passed or failed)
     */
    @PostExchange("/check")
    ComplianceCheckResponseVO checkCompliance(@RequestBody ComplianceCheckRequestDTO request);
}
