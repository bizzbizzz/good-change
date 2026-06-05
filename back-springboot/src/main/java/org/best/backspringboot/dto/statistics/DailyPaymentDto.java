package org.best.backspringboot.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyPaymentDto {
    private String date;          // yyyyMMdd
    private Long amount;          // 순 결제금액 (성공 - 취소)
    private Long count;           // 순 결제건수 (성공 - 취소)
    private Long successAmount;   // 성공 결제금액
    private Long successCount;    // 성공 결제건수
    private Long cancelAmount;    // 취소 금액
    private Long cancelCount;     // 취소 건수
}
