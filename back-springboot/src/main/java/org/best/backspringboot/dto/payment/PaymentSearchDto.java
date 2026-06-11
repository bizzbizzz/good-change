package org.best.backspringboot.dto.payment;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.SearchBase;

@Getter
@Setter
public class PaymentSearchDto extends SearchBase {
    private String startDate;
    private Long memberId;      // 추가
    private String categoryId;
    private String endDate;
    private String transactionType;
    private String merchantName;
    private Long merchantId;
    private String approvalNumber;
    private String status;
    private String memberName;   // 추가
    private String cardNumber;   // 추가
}