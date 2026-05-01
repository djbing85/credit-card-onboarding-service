package com.jasper.config;

import com.jasper.thirdparty.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Third-party Service Client Configuration
 * Creates HTTP client proxies for third-party service interfaces
 */
@Configuration
public class ThirdPartyServiceConfig {

    @Value("${app.third-party.base-url:http://localhost:8081}")
    private String baseUrl;

    @Value("${app.third-party.connect-timeout:3000}")
    private int connectTimeout;

    @Value("${app.third-party.identity-verification-read-timeout:5000}")
    private int identityVerificationReadTimeout;

    @Value("${app.third-party.employment-verification-read-timeout:8000}")
    private int employmentVerificationReadTimeout;

    @Value("${app.third-party.compliance-check-read-timeout:6000}")
    private int complianceCheckReadTimeout;

    @Value("${app.third-party.behavioral-analysis-read-timeout:10000}")
    private int behavioralAnalysisReadTimeout;

    @Value("${app.third-party.risk-evaluation-read-timeout:7000}")
    private int riskEvaluationReadTimeout;

    /**
     * Create HttpServiceProxyFactory with base URL and timeout
     */
    private HttpServiceProxyFactory createProxyFactory(int readTimeoutMillis) {
        ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod) throws java.io.IOException {
                super.prepareConnection(connection, httpMethod);
                connection.setConnectTimeout(connectTimeout);
                connection.setReadTimeout(readTimeoutMillis);
            }
        };

        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
        
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    /**
     * Employment Verification Service Client
     */
    @Bean
    public EmploymentVerificationServiceClient employmentVerificationServiceClient() {
        HttpServiceProxyFactory factory = createProxyFactory(employmentVerificationReadTimeout);
        return factory.createClient(EmploymentVerificationServiceClient.class);
    }

    /**
     * Identity Verification Service Client
     */
    @Bean
    public IdentityVerificationServiceClient identityVerificationServiceClient() {
        HttpServiceProxyFactory factory = createProxyFactory(identityVerificationReadTimeout);
        return factory.createClient(IdentityVerificationServiceClient.class);
    }

    /**
     * Behavioral Analysis Service Client
     */
    @Bean
    public BehavioralAnalysisServiceClient behavioralAnalysisServiceClient() {
        HttpServiceProxyFactory factory = createProxyFactory(behavioralAnalysisReadTimeout);
        return factory.createClient(BehavioralAnalysisServiceClient.class);
    }

    /**
     * Compliance Check Service Client
     */
    @Bean
    public ComplianceCheckServiceClient complianceCheckServiceClient() {
        HttpServiceProxyFactory factory = createProxyFactory(complianceCheckReadTimeout);
        return factory.createClient(ComplianceCheckServiceClient.class);
    }

    /**
     * Risk Evaluation Service Client
     */
    @Bean
    public RiskEvaluationServiceClient riskEvaluationServiceClient() {
        HttpServiceProxyFactory factory = createProxyFactory(riskEvaluationReadTimeout);
        return factory.createClient(RiskEvaluationServiceClient.class);
    }
}
