package org.best.backspringboot.dto.merchant;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.SearchBase;

@Getter
@Setter
public class MerchantSearchDto extends SearchBase {
    private String merchantName;
    private String businessNumber;
    private String categoryName;  // ✅ 카테고리 검색 추가
}