package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.dto.SearchBase;
import org.best.backspringboot.dto.merchant.MerchantCreateDto;
import org.best.backspringboot.dto.merchant.MerchantUpdateDto;
import org.best.backspringboot.entity.Merchant;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MerchantMapper {
    void insert(MerchantCreateDto dto);
    Optional<Merchant> findById(Long merchantId);
    Optional<Merchant> findByMemberId(Long memberId);
    Optional<Merchant> findByBusinessNumber(String businessNumber);
    List<Merchant> findAll(SearchBase searchBase);
    long countAll();
    void update(Long merchantId, MerchantUpdateDto dto);
    void delete(Long merchantId);
    List<String> findCategoriesByMerchantId(Long merchantId);
    void insertCategory(@Param("merchantId") Long merchantId,
                        @Param("categoryName") String categoryName);
    void deleteCategories(Long merchantId);
}