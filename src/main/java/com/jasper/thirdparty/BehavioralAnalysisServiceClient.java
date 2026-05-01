package com.jasper.thirdparty;

import com.jasper.thirdparty.dto.BehavioralAnalysisRequestDTO;
import com.jasper.thirdparty.vo.BehavioralAnalysisResponseVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Behavioral Analysis Service Client
 *
 * Implement an analysis of spending habits and payment history to predict
 * future credit behavior.
 */
@HttpExchange("/behavior")
public interface BehavioralAnalysisServiceClient {
    
    /**
     * Analyze spending habits and payment history
     *
     * @param request behavioral analysis request parameters
     * @return behavioral analysis score (0.0000 ~ 1.0000, representing 0% - 100%)
     */
    @PostExchange("/analyze")
    BehavioralAnalysisResponseVO analyzeBehavior(@RequestBody BehavioralAnalysisRequestDTO request);
}
