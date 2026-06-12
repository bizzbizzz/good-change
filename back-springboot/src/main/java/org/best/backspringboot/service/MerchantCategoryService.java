package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.entity.MerchantCategory;
import org.best.backspringboot.mapper.MerchantCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantCategoryService {

    private final MerchantCategoryMapper merchantCategoryMapper;

    @Transactional(readOnly = true)
    public List<MerchantCategory> getAll() {
        return merchantCategoryMapper.findAll();
    }

    @Transactional
    public void create(String categoryName, String description) {
        MerchantCategory category = MerchantCategory.builder()
                .categoryName(categoryName)
                .description(description)
                .build();
        merchantCategoryMapper.insert(category);
    }

    @Transactional
    public void update(Long categoryId, String categoryName, String description) {
        merchantCategoryMapper.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        merchantCategoryMapper.update(categoryId, categoryName, description);
    }

    @Transactional
    public void delete(Long categoryId) {
        merchantCategoryMapper.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        merchantCategoryMapper.delete(categoryId);
    }
}
