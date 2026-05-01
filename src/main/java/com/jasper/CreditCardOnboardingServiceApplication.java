package com.jasper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Credit Card Onboarding Service
 */
@SpringBootApplication
@EnableScheduling
public class CreditCardOnboardingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditCardOnboardingServiceApplication.class, args);
    }
}
