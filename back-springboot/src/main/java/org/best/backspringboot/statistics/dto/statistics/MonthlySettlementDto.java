package org.best.backspringboot.statistics.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlySettlementDto {
    private String month;   // YYYY-MM
    private Long amount;    // 총 정산금액
    private Long count;     // 정산 건수
}
