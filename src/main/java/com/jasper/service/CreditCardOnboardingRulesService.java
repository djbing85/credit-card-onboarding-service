package com.jasper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jasper.entity.CreditCardOnboardingRules;
import com.jasper.mapper.CreditCardOnboardingRulesMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Credit Card Onboarding Rules Service
 */
@Service
@Transactional
public class CreditCardOnboardingRulesService extends ServiceImpl<CreditCardOnboardingRulesMapper, CreditCardOnboardingRules> {

    /**
     * Create rule
     */
    public CreditCardOnboardingRules create(CreditCardOnboardingRules rules) {
        long now = Instant.now().toEpochMilli();
        rules.setCreatedTime(now);
        rules.setUpdatedTime(now);
        rules.setVersion(0L);
        rules.setStatus(CreditCardOnboardingRules.STATUS_ENABLED);
        rules.setOperator("system");
        save(rules);
        return rules;
    }

    /**
     * Get by ID
     */
    public CreditCardOnboardingRules getById(Integer id) {
        return super.getById(id);
    }

    /**
     * List rules with pagination
     */
    public Page<CreditCardOnboardingRules> pageList(Integer pageNum, Integer pageSize) {
        Page<CreditCardOnboardingRules> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CreditCardOnboardingRules> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(CreditCardOnboardingRules::getStatus, CreditCardOnboardingRules.STATUS_DELETED)
               .orderByDesc(CreditCardOnboardingRules::getId);
        return super.page(page, wrapper);
    }

    /**
     * Update rule
     */
    public CreditCardOnboardingRules update(CreditCardOnboardingRules rules) {
        rules.setUpdatedTime(Instant.now().toEpochMilli());
        updateById(rules);
        return rules;
    }

    /**
     * Delete rule
     */
    public boolean delete(Integer id) {
        return removeById(id);
    }
}
