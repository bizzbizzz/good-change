package org.best.backspringboot.dto.merchant;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.SearchBase;

@Getter
@Setter
public class MerchantSearchDto extends SearchBase {
    private String merchantName;
    private String representative;
    private String businessNumber;
    private String categoryName;
    private String contact;
    private String terminalId;
    private String keyword;  // 전체 검색용
    private String searchType;  // name/address
    private Long categoryId;
}