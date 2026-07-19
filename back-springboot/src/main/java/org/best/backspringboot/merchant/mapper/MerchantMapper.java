package org.best.backspringboot.merchant.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.merchant.dto.merchant.MerchantCreateDto;
import org.best.backspringboot.merchant.dto.merchant.MerchantSearchDto;
import org.best.backspringboot.merchant.dto.merchant.MerchantUpdateDto;
import org.best.backspringboot.merchant.entity.Merchant;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MerchantMapper {
    void insert(MerchantCreateDto dto);
    Optional<Merchant> findById(Long merchantId);
    Optional<Merchant> findByMemberId(Long memberId);
    Optional<Merchant> findByBusinessNumber(String businessNumber);
    List<Merchant> findAll(MerchantSearchDto searchDto);  // SearchBase → MerchantSearchDto
    long countAll();
    void update(Long merchantId, MerchantUpdateDto dto);
    void delete(Long merchantId);
    Long findMerchantIdByMemberId(Long memberId);
    List<String> findAllCategories();
    String findCategoryNameById(Long categoryId);
}