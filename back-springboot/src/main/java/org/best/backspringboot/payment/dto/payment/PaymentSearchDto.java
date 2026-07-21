package org.best.backspringboot.payment.dto.payment;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.global.commonDTO.SearchBase;

@Getter
@Setter
public class PaymentSearchDto extends SearchBase {
    private String startDate;
    private Long memberId;
    private String categoryId;      // category_id (1/2/3) 으로 필터링
    private String endDate;
    private String transactionType;
    private String merchantName;
    private Long merchantId;
    private String approvalNumber;
    private String status;
    private String memberName;
    private String cardNumber;
    private String searchType;      // address / name / approval
    private String keyword;         // 검색어
}