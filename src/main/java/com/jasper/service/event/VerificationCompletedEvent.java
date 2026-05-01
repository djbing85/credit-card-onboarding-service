package com.jasper.service.event;

import com.jasper.entity.CreditCardOnboarding;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Verification Completed Event
 * Published when verification process completes
 */
@Getter
public class VerificationCompletedEvent extends ApplicationEvent {
    
    private final CreditCardOnboarding onboarding;
    private final String verifiedResult;
    private final String totalScore;
    private final String verifiedDetail;
    
    public VerificationCompletedEvent(Object source, 
                                     CreditCardOnboarding onboarding,
                                     String verifiedResult,
                                     String totalScore,
                                     String verifiedDetail) {
        super(source);
        this.onboarding = onboarding;
        this.verifiedResult = verifiedResult;
        this.totalScore = totalScore;
        this.verifiedDetail = verifiedDetail;
    }
}
