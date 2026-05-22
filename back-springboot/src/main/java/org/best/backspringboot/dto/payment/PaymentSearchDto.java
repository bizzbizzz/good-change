package org.best.backspringboot.dto.payment;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.SearchBase;

@Getter
@Setter
public class PaymentSearchDto extends SearchBase {
    private String startDate;
    private String endDate;
    private String transactionType;
    private String merchantName;
    private String approvalNumber;
    private String status;
}