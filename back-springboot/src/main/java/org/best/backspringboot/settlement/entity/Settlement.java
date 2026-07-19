package org.best.backspringboot.settlement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Settlement {
    private Long settlementId;
    private Long merchantId;
    private String merchantName;
    private String businessNumber;
    private String loginId;
    private String settlementMonth;
    private Long settlementAmount;
    private String status;
    private LocalDateTime statusChangedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer successCount;
    private Integer cancelCount;
}