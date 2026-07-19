package org.best.backspringboot.merchant.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.merchant.entity.MerchantCategory;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MerchantCategoryMapper {
    List<MerchantCategory> findAll();
    Optional<MerchantCategory> findById(@Param("categoryId") Long categoryId);
    void insert(MerchantCategory category);
    void update(@Param("categoryId") Long categoryId,
                @Param("categoryName") String categoryName,
                @Param("description") String description);
    void delete(@Param("categoryId") Long categoryId);
}
