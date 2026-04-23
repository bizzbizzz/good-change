package org.best.backspringboot.dto.merchant;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.entity.Merchant;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MerchantResponseDto {
    private Long merchantId;
    private String loginId;
    private String merchantName;
    private String representative;
    private String businessNumber;
    private String contact;
    private String phone;
    private String address;
    private String homepage;
    private String managerName;
    private String email;
    private Long referrerId;
    private String terminalId;
    private LocalDateTime createdAt;
    private List<String> categories;

    public static MerchantResponseDto from(Merchant merchant, List<String> categories) {
        return MerchantResponseDto.builder()
                .merchantId(merchant.getMerchantId())
                .loginId(merchant.getLoginId())
                .merchantName(merchant.getMerchantName())
                .representative(merchant.getRepresentative())
                .businessNumber(merchant.getBusinessNumber())
                .contact(merchant.getContact())
                .phone(merchant.getPhone())
                .address(merchant.getAddress())
                .homepage(merchant.getHomepage())
                .managerName(merchant.getManagerName())
                .email(merchant.getEmail())
                .referrerId(merchant.getReferrerId())
                .terminalId(merchant.getTerminalId())
                .createdAt(merchant.getCreatedAt())
                .categories(categories)
                .build();
    }
}