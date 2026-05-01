package com.jasper.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jasper.dto.CreditCardOnboardingRulesDTO;
import com.jasper.entity.CreditCardOnboardingRules;
import com.jasper.vo.CreditCardOnboardingRulesVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Credit Card Onboarding Rules Object Converter
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreditCardOnboardingRulesConverter {
    
    /**
     * DTO to Entity
     */
    CreditCardOnboardingRules toEntity(CreditCardOnboardingRulesDTO dto);
    
    /**
     * Entity to VO
     */
    CreditCardOnboardingRulesVO toVO(CreditCardOnboardingRules entity);
    
    /**
     * Entity list to VO list
     */
    List<CreditCardOnboardingRulesVO> toVOList(List<CreditCardOnboardingRules> entities);
    
    /**
     * Page object conversion
     */
    default Page<CreditCardOnboardingRulesVO> toVOPage(Page<CreditCardOnboardingRules> page) {
        Page<CreditCardOnboardingRulesVO> voPage = new Page<>();
        voPage.setCurrent(page.getCurrent());
        voPage.setSize(page.getSize());
        voPage.setTotal(page.getTotal());
        voPage.setPages(page.getPages());
        voPage.setRecords(toVOList(page.getRecords()));
        return voPage;
    }
}
