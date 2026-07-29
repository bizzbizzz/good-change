package org.best.backspringboot.merchant.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
    Optional<Merchant> findByBusinessNumber(String businessNumber);
    List<Merchant> findAll(MerchantSearchDto searchDto);
    long countAll(MerchantSearchDto searchDto);
    void update(@Param("merchantId") Long merchantId, @Param("dto") MerchantUpdateDto dto);
    void delete(Long merchantId);
    List<String> findAllCategories();
    String findCategoryNameById(Long categoryId);
    void updateStatus(@Param("merchantId") Long merchantId, @Param("status") String status);
    void withdraw(Long merchantId);
}