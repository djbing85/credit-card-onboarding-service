package com.jasper.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jasper.dto.CreditCardOnboardingDTO;
import com.jasper.entity.CreditCardOnboarding;
import com.jasper.vo.CreditCardOnboardingVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Credit Card Onboarding Object Converter
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreditCardOnboardingConverter {
    
    /**
     * DTO to Entity
     */
    CreditCardOnboarding toEntity(CreditCardOnboardingDTO dto);
    
    /**
     * Entity to VO
     */
    CreditCardOnboardingVO toVO(CreditCardOnboarding entity);
    
    /**
     * Entity list to VO list
     */
    List<CreditCardOnboardingVO> toVOList(List<CreditCardOnboarding> entities);
    
    /**
     * Page object conversion
     */
    default Page<CreditCardOnboardingVO> toVOPage(Page<CreditCardOnboarding> page) {
        Page<CreditCardOnboardingVO> voPage = new Page<>();
        voPage.setCurrent(page.getCurrent());
        voPage.setSize(page.getSize());
        voPage.setTotal(page.getTotal());
        voPage.setPages(page.getPages());
        voPage.setRecords(toVOList(page.getRecords()));
        return voPage;
    }
}
