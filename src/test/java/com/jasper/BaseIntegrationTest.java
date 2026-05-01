package com.jasper;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * Base Integration Test Class
 * All test classes share the same Spring ApplicationContext.
 * Database is cleaned before each test class to ensure isolation.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {CreditCardOnboardingServiceApplication.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest
        // fallback data modification in all test cases
        extends AbstractTransactionalJUnit4SpringContextTests
{

    @LocalServerPort
    protected int port;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /**
     * Clean up test data before each test method to ensure data isolation.
     * This allows all test classes to share the same ApplicationContext and H2 database.
     */
    @BeforeEach
    void cleanTestData() {
        // Delete test onboarding records
        jdbcTemplate.execute("DELETE FROM credit_card_onboarding WHERE emirates_id_number LIKE '784-TEST-%' OR emirates_id_number LIKE '784-VT-%' OR emirates_id_number LIKE '784-STV-%'");
        
        // Delete test rules (keep init_data.sql rules)
        jdbcTemplate.execute("DELETE FROM credit_card_onboarding_rules WHERE criteria LIKE '%Test%' AND id > 5");
    }
}
