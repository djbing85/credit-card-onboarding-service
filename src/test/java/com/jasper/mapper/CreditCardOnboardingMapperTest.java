package com.jasper.mapper;

import com.jasper.BaseIntegrationTest;
import com.jasper.entity.CreditCardOnboarding;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class CreditCardOnboardingMapperTest extends BaseIntegrationTest {

    @Autowired
    private CreditCardOnboardingMapper mapper;

    @Test
    void testInsertAndSelect() {
        CreditCardOnboarding onboarding = new CreditCardOnboarding();
        onboarding.setEmiratesIdNumber("784-TEST-001");
        onboarding.setName("Test User");
        onboarding.setMobileNumber("+971500000000");
        
        mapper.insert(onboarding);
        
        CreditCardOnboarding found = mapper.selectById(onboarding.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test User");
    }
}
