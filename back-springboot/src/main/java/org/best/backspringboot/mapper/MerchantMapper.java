package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
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
    List<Merchant> findAll(SearchBase searchBase);
    void update(Long merchantId, MerchantUpdateDto dto);
    void delete(Long merchantId);
    Optional<Merchant> findByLoginId(String loginId);
    Optional<Merchant> findByBusinessNumber(String businessNumber);
    void insertCategory(Long merchantId, String categoryName);
    void deleteCategories(Long merchantId);
    List<String> findCategoriesByMerchantId(Long merchantId);
    long countAll();
}