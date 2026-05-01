package com.jasper.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jasper.common.CommonResponse;
import com.jasper.converter.CreditCardOnboardingConverter;
import com.jasper.dto.CreditCardOnboardingDTO;
import com.jasper.entity.CreditCardOnboarding;
import com.jasper.service.CreditCardOnboardingService;
import com.jasper.service.CreditCardOnboardingVerifyService;
import com.jasper.vo.CreditCardOnboardingVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Credit Card Onboarding Controller
 */
@RestController
@RequestMapping("/api/onboarding")
public class CreditCardOnboardingController {

    @Autowired
    private CreditCardOnboardingService onboardingService;
    
    @Autowired
    private CreditCardOnboardingConverter onboardingConverter;
    
    @Autowired
    private CreditCardOnboardingVerifyService verifyService;

    /**
     * Create onboarding application
     */
    @PostMapping
    public CommonResponse<CreditCardOnboardingVO> create(@Valid @RequestBody CreditCardOnboardingDTO dto) {
        CreditCardOnboarding entity = onboardingConverter.toEntity(dto);
        CreditCardOnboarding result = onboardingService.create(entity);
        CreditCardOnboardingVO vo = onboardingConverter.toVO(result);
        return CommonResponse.success(vo);
    }

    /**
     * Get by ID
     */
    @GetMapping("/{id}")
    public CommonResponse<CreditCardOnboardingVO> getById(@PathVariable Long id) {
        CreditCardOnboarding result = onboardingService.getById(id);
        if (result != null) {
            CreditCardOnboardingVO vo = onboardingConverter.toVO(result);
            return CommonResponse.success(vo);
        }
        return CommonResponse.notFound("Onboarding application not found");
    }

    /**
     * Page query onboarding list (order by ID desc, exclude status=9)
     * @param pageNum page number, default 1
     * @param pageSize page size, default 10
     * @return paginated data
     * TODO deep paging issue
     */
    @GetMapping("/page")
    public CommonResponse<Page<CreditCardOnboardingVO>> pageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CreditCardOnboarding> result = onboardingService.pageList(pageNum, pageSize);
        Page<CreditCardOnboardingVO> voPage = onboardingConverter.toVOPage(result);
        return CommonResponse.success(voPage);
    }

    /**
     * Update onboarding application
     */
    @PutMapping
    public CommonResponse<CreditCardOnboardingVO> update(@Valid @RequestBody CreditCardOnboardingDTO dto) {
        CreditCardOnboarding entity = onboardingConverter.toEntity(dto);
        CreditCardOnboarding result = onboardingService.update(entity);
        CreditCardOnboardingVO vo = onboardingConverter.toVO(result);
        return CommonResponse.success(vo);
    }

    /**
     * Delete onboarding application
     */
    @DeleteMapping("/{id}")
    public CommonResponse<Void> delete(@PathVariable Long id) {
        boolean success = onboardingService.delete(id);
        if (success) {
            return CommonResponse.success();
        }
        return CommonResponse.notFound("Onboarding application not found");
    }

    /**
     * Verify onboarding application through all rule services
     *
     * @param id onboarding application ID
     * @return verification result with score and details
     */
    @PostMapping("/{id}/verify")
    public CommonResponse<CreditCardOnboardingVO> verify(@PathVariable Long id) {
        CreditCardOnboardingVerifyService.VerificationResult result = verifyService.verify(id);
        
        if (result.isNotFound()) {
            return CommonResponse.notFound("Onboarding application not found");
        }
        
        // Convert to VO and return
        CreditCardOnboardingVO vo = onboardingConverter.toVO(result.getOnboarding());
        return CommonResponse.success(vo);
    }
}
