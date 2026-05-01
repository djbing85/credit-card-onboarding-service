package com.jasper.thirdparty.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jasper.entity.CreditCardOnboardingRules;
import com.jasper.mapper.CreditCardOnboardingRulesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Rule Cache Manager
 * Manages caching of credit card onboarding rules using Caffeine
 */
@Slf4j
@Component
public class RuleCacheManager implements InitializingBean {
    
    private static final String CACHE_KEY_ALL_RULES = "all_rules";
    private static final String CACHE_KEY_MANDATORY_RULES = "mandatory_rules";
    private static final String CACHE_KEY_NON_MANDATORY_RULES = "non_mandatory_rules";
    
    private Cache<String, List<CreditCardOnboardingRules>> rulesCache;
    
    @Value("${app.rule.cache-expire-millis:60000}")
    private long cacheExpireMillis;
    
    @Autowired
    private CreditCardOnboardingRulesMapper rulesMapper;
    
    @Override
    public void afterPropertiesSet() {
        // Initialize Caffeine cache with configurable expiration time
        rulesCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheExpireMillis, TimeUnit.MILLISECONDS)
                .maximumSize(100)
                .build();
        
        log.info("RuleCacheManager initialized with cache expiration: {} ms", cacheExpireMillis);
    }
    
    /**
     * Get all enabled rules from cache (loads from DB if not cached)
     */
    public List<CreditCardOnboardingRules> getAllEnabledRules() {
        return rulesCache.get(CACHE_KEY_ALL_RULES, key -> {
            log.debug("Loading all enabled rules from database for cache");
            LambdaQueryWrapper<CreditCardOnboardingRules> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CreditCardOnboardingRules::getStatus, CreditCardOnboardingRules.STATUS_ENABLED);
            return rulesMapper.selectList(wrapper);
        });
    }
    
    /**
     * Get mandatory rules from cache (loads from DB if not cached)
     */
    public List<CreditCardOnboardingRules> getMandatoryRules() {
        return rulesCache.get(CACHE_KEY_MANDATORY_RULES, key -> {
            log.debug("Loading mandatory rules from database for cache");
            LambdaQueryWrapper<CreditCardOnboardingRules> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CreditCardOnboardingRules::getStatus, CreditCardOnboardingRules.STATUS_ENABLED)
                   .eq(CreditCardOnboardingRules::getMandatoryPass, true);
            return rulesMapper.selectList(wrapper);
        });
    }
    
    /**
     * Get non-mandatory rules from cache (loads from DB if not cached)
     */
    public List<CreditCardOnboardingRules> getNonMandatoryRules() {
        return rulesCache.get(CACHE_KEY_NON_MANDATORY_RULES, key -> {
            log.debug("Loading non-mandatory rules from database for cache");
            LambdaQueryWrapper<CreditCardOnboardingRules> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CreditCardOnboardingRules::getStatus, CreditCardOnboardingRules.STATUS_ENABLED)
                   .eq(CreditCardOnboardingRules::getMandatoryPass, false);
            return rulesMapper.selectList(wrapper);
        });
    }
    
    /**
     * Invalidate all caches (useful when rules are updated)
     */
    public void invalidateAll() {
        rulesCache.invalidateAll();
        log.info("All rule caches invalidated");
    }
    
    /**
     * Scheduled task to refresh cache periodically
     * Runs every cacheExpireMillis milliseconds
     */
    @Scheduled(fixedDelayString = "${app.rule.cache-expire-millis:60000}")
    public void refreshCache() {
        log.debug("Scheduled cache refresh triggered");
        invalidateAll();
        log.info("Cache refreshed successfully at {}", java.time.LocalDateTime.now());
    }
}
