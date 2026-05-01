package com.jasper.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jasper.common.CommonResponse;
import com.jasper.converter.CreditCardOnboardingRulesConverter;
import com.jasper.dto.CreditCardOnboardingRulesDTO;
import com.jasper.entity.CreditCardOnboardingRules;
import com.jasper.service.CreditCardOnboardingRulesService;
import com.jasper.vo.CreditCardOnboardingRulesVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Credit Card Onboarding Rules Controller
 */
@RestController
@RequestMapping("/api/rules")
public class CreditCardOnboardingRulesController {

    @Autowired
    private CreditCardOnboardingRulesService rulesService;
    
    @Autowired
    private CreditCardOnboardingRulesConverter rulesConverter;

    /**
     * Create rule
     */
    @PostMapping
    public CommonResponse<CreditCardOnboardingRulesVO> create(@Valid @RequestBody CreditCardOnboardingRulesDTO dto) {
        CreditCardOnboardingRules entity = rulesConverter.toEntity(dto);
        CreditCardOnboardingRules result = rulesService.create(entity);
        CreditCardOnboardingRulesVO vo = rulesConverter.toVO(result);
        return CommonResponse.success(vo);
    }

    /**
     * Get by ID
     */
    @GetMapping("/{id}")
    public CommonResponse<CreditCardOnboardingRulesVO> getById(@PathVariable Integer id) {
        CreditCardOnboardingRules result = rulesService.getById(id);
        if (result != null) {
            CreditCardOnboardingRulesVO vo = rulesConverter.toVO(result);
            return CommonResponse.success(vo);
        }
        return CommonResponse.notFound("Rule not found");
    }

    /**
     * List rules with pagination
     */
    @GetMapping("/page")
    public CommonResponse<Page<CreditCardOnboardingRulesVO>> pageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CreditCardOnboardingRules> result = rulesService.pageList(pageNum, pageSize);
        Page<CreditCardOnboardingRulesVO> voPage = rulesConverter.toVOPage(result);
        return CommonResponse.success(voPage);
    }

    /**
     * Update rule
     */
    @PutMapping
    public CommonResponse<CreditCardOnboardingRulesVO> update(@Valid @RequestBody CreditCardOnboardingRulesDTO dto) {
        CreditCardOnboardingRules entity = rulesConverter.toEntity(dto);
        CreditCardOnboardingRules result = rulesService.update(entity);
        CreditCardOnboardingRulesVO vo = rulesConverter.toVO(result);
        return CommonResponse.success(vo);
    }

    /**
     * Delete rule
     */
    @DeleteMapping("/{id}")
    public CommonResponse<Void> delete(@PathVariable Integer id) {
        boolean success = rulesService.delete(id);
        if (success) {
            return CommonResponse.success();
        }
        return CommonResponse.notFound("Rule not found");
    }
}
