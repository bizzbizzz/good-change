package org.best.backspringboot.dto.merchant;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.entity.Merchant;
import java.time.LocalDateTime;

@Getter
@Builder
public class MerchantResponseDto {
    private Long merchantId;
    private Long memberId;
    private String merchantName;
    private String representative;
    private String businessNumber;
    private String contact;
    private String address;
    private String email;
    private String status;
    private Long referrerId;
    private String terminalId;
    private LocalDateTime createdAt;
    private String ip;
    private LocalDateTime applyDate;
    private LocalDateTime approveDate;

    public static MerchantResponseDto from(Merchant merchant) {
        return MerchantResponseDto.builder()
                .merchantId(merchant.getMerchantId())
                .memberId(merchant.getMemberId())
                .merchantName(merchant.getMerchantName())
                .representative(merchant.getRepresentative())
                .businessNumber(merchant.getBusinessNumber())
                .contact(merchant.getContact())
                .address(merchant.getAddress())
                .email(merchant.getEmail())
                .status(merchant.getStatus())
                .referrerId(merchant.getReferrerId())
                .terminalId(merchant.getTerminalId())
                .createdAt(merchant.getCreatedAt())
                .ip(merchant.getIp())
                .applyDate(merchant.getApplyDate())
                .approveDate(merchant.getApproveDate())
                .build();
    }
}