package com.jasper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jasper.common.CustomException;
import com.jasper.entity.CreditCardOnboarding;
import com.jasper.mapper.CreditCardOnboardingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Credit Card Onboarding Service
 */
@Service
@Transactional
public class CreditCardOnboardingService extends ServiceImpl<CreditCardOnboardingMapper, CreditCardOnboarding> {

    /**
     * Create onboarding application
     */
    public CreditCardOnboarding create(CreditCardOnboarding onboarding) {
        // Check if Emirates ID already exists (excluding deleted records)
        LambdaQueryWrapper<CreditCardOnboarding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CreditCardOnboarding::getEmiratesIdNumber, onboarding.getEmiratesIdNumber())
               .ne(CreditCardOnboarding::getStatus, CreditCardOnboarding.STATUS_DELETED);
        
        long count = count(wrapper);
        if (count > 0) {
            throw CustomException.duplicateEntry("Emirates ID number already exists: " + onboarding.getEmiratesIdNumber());
        }
        
        long now = Instant.now().toEpochMilli();
        onboarding.setCreatedTime(now);
        onboarding.setUpdatedTime(now);
        onboarding.setVersion(0L);
        onboarding.setStatus(CreditCardOnboarding.STATUS_ENABLED);
        onboarding.setOperator("system");
        save(onboarding);
        return onboarding;
    }

    /**
     * Get by ID
     */
    public CreditCardOnboarding getById(Long id) {
        return super.getById(id);
    }

    /**
     * Page query onboarding list (order by ID desc, exclude status=9)
     * @param pageNum page number
     * @param pageSize page size
     * @return paginated result
     */
    public Page<CreditCardOnboarding> pageList(Integer pageNum, Integer pageSize) {
        Page<CreditCardOnboarding> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CreditCardOnboarding> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(CreditCardOnboarding::getStatus, CreditCardOnboarding.STATUS_DELETED)
               .orderByDesc(CreditCardOnboarding::getId);
        return page(page, wrapper);
    }

    /**
     * Update onboarding application
     */
    public CreditCardOnboarding update(CreditCardOnboarding onboarding) {
        // Get current version from database to avoid null pointer exception
        CreditCardOnboarding existing = getById(onboarding.getId());
        if (existing == null) {
            throw new RuntimeException("Onboarding not found with id: " + onboarding.getId());
        }
        
        // Use existing version and increment it
        onboarding.setVersion(existing.getVersion() + 1);
        onboarding.setUpdatedTime(Instant.now().toEpochMilli());
        updateById(onboarding);
        return onboarding;
    }

    /**
     * Delete onboarding application
     */
    public boolean delete(Long id) {
        return removeById(id);
    }

    /**
     * Update verification result
     *
     * @param id onboarding ID
     * @param verifiedResult verified result (true/false)
     * @param verifiedScore verified score (0.0000 ~ 1.0000)
     * @param verifiedDetail verified detail
     */
    public void updateVerificationResult(Long id, String verifiedResult, String verifiedScore, String verifiedDetail) {
        CreditCardOnboarding onboarding = getById(id);
        if (onboarding != null) {
            onboarding.setVerifiedResult(verifiedResult);
            onboarding.setVerifiedScore(verifiedScore);
            onboarding.setVerifiedDetail(verifiedDetail);
            onboarding.setVerifiedTime(Instant.now().toEpochMilli());
            onboarding.setUpdatedTime(Instant.now().toEpochMilli());
            onboarding.setVersion(onboarding.getVersion() + 1);
            updateById(onboarding);
        }
    }

    /**
     * Update all verification fields
     *
     * @param id onboarding ID
     * @param verifiedResult verified result (true/false/rejected)
     * @param verifiedScore verified score (0.0000 ~ 1.0000)
     * @param verifiedDetail verified detail (JSON format with all rule results)
     */
    public void updateAllVerificationFields(Long id, String verifiedResult, String verifiedScore, String verifiedDetail) {
        CreditCardOnboarding onboarding = getById(id);
        if (onboarding != null) {
            long now = Instant.now().toEpochMilli();
            onboarding.setVerifiedResult(verifiedResult);
            onboarding.setVerifiedScore(verifiedScore);
            onboarding.setVerifiedDetail(verifiedDetail);
            onboarding.setVerifiedTime(now);
            onboarding.setUpdatedTime(now);
            onboarding.setVersion(onboarding.getVersion() + 1);
            updateById(onboarding);
        }
    }
}
