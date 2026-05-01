package com.jasper.mapper;

import com.jasper.BaseIntegrationTest;
import com.jasper.entity.CreditCardOnboardingRules;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CreditCardOnboardingRulesMapperTest extends BaseIntegrationTest {

    @Autowired
    private CreditCardOnboardingRulesMapper mapper;

    @Test
    void testInsertAndSelect() {
        CreditCardOnboardingRules rule = new CreditCardOnboardingRules();
        rule.setCriteria("Test Rule");
        rule.setMandatoryPass(true);
        rule.setScoreContribution(new BigDecimal("0.1000"));
        rule.setScoreType(0);
        rule.setScore(new BigDecimal("10.00"));
        
        mapper.insert(rule);
        
        CreditCardOnboardingRules found = mapper.selectById(rule.getId());
        assertThat(found).isNotNull();
        assertThat(found.getCriteria()).isEqualTo("Test Rule");
        assertThat(found.getMandatoryPass()).isTrue();
    }
}
