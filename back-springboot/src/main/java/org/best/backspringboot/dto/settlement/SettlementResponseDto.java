package org.best.backspringboot.dto.settlement;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.entity.Settlement;
import java.time.LocalDateTime;

@Getter
@Builder
public class SettlementResponseDto {
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

    public static SettlementResponseDto from(Settlement settlement) {
        return SettlementResponseDto.builder()
                .settlementId(settlement.getSettlementId())
                .merchantId(settlement.getMerchantId())
                .merchantName(settlement.getMerchantName())
                .businessNumber(settlement.getBusinessNumber())
                .loginId(settlement.getLoginId())
                .settlementMonth(settlement.getSettlementMonth())
                .settlementAmount(settlement.getSettlementAmount())
                .status(settlement.getStatus())
                .statusChangedAt(settlement.getStatusChangedAt())
                .createdAt(settlement.getCreatedAt())
                .build();
    }
}