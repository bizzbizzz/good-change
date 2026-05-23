package org.best.backspringboot.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyPaymentDto {
    private String date;    // yyyyMMdd
    private Long amount;    // 총 결제금액
    private Long count;     // 결제 건수
}
