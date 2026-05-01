package com.jasper.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Score Strategy Manager
 * Manages score calculation strategies and provides strategy selection
 */
@Slf4j
@Component
public class ScoreStrategyManager {
    
    private final Map<Integer, ScoreCalculationStrategy> strategyMap = new HashMap<>();
    
    @Autowired
    private List<ScoreCalculationStrategy> strategies;
    
    @PostConstruct
    public void init() {
        // Register all strategies by their score type
        for (ScoreCalculationStrategy strategy : strategies) {
            strategyMap.put(strategy.getScoreType(), strategy);
            log.info("Registered score strategy for type: {}", strategy.getScoreType());
        }
    }
    
    /**
     * Get strategy by score type
     *
     * @param scoreType the score type constant
     * @return the corresponding strategy
     * @throws IllegalArgumentException if no strategy found for the given type
     */
    public ScoreCalculationStrategy getStrategy(int scoreType) {
        ScoreCalculationStrategy strategy = strategyMap.get(scoreType);
        if (strategy == null) {
            throw new IllegalArgumentException("No score strategy found for type: " + scoreType);
        }
        return strategy;
    }
}
