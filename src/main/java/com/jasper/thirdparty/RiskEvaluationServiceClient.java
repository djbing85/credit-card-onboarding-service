package com.jasper.thirdparty;

import com.jasper.thirdparty.dto.RiskEvaluationRequestDTO;
import com.jasper.thirdparty.vo.RiskEvaluationResponseVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Risk Evaluation Service Client
 *
 * Evaluate each applicant's financial history to determine creditworthiness
 * using AECB data.
 */
@HttpExchange("/risk")
public interface RiskEvaluationServiceClient {
    
    /**
     * Evaluate credit risk based on financial history
     *
     * @param request risk evaluation request parameters
     * @return risk score (0.0000 ~ 1.0000, representing 0% - 100%)
     */
    @PostExchange("/evaluate")
    RiskEvaluationResponseVO evaluateRisk(@RequestBody RiskEvaluationRequestDTO request);
}
