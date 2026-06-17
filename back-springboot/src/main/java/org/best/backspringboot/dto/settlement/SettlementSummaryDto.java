package org.best.backspringboot.dto.settlement;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementSummaryDto {
    private Long thisMonthAmount;  // 당월 정산금액
    private Long lastMonthAmount;  // 전월 정산금액
}