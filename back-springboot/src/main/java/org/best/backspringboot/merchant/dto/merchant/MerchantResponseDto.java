package org.best.backspringboot.merchant.dto.merchant;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.merchant.entity.Merchant;
import java.time.LocalDateTime;
import java.util.List;

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
    private String categoryName;  // 추가
    private Long categoryId;  // 추가
    private String status;
    private Long referrerId;
    private String terminalId;
    private LocalDateTime createdAt;
    private LocalDateTime applyDate;
    private LocalDateTime approveDate;
    private List<String> categories;  // ✅ 추가
    private String loginId;  // 추가


    public static MerchantResponseDto from(Merchant merchant, List<String> categories) {
        return MerchantResponseDto.builder()
                .merchantId(merchant.getMerchantId())
                .merchantName(merchant.getMerchantName())
                .representative(merchant.getRepresentative())
                .businessNumber(merchant.getBusinessNumber())
                .contact(merchant.getContact())
                .categoryName(merchant.getCategoryName())
                .address(merchant.getAddress())
                .email(merchant.getEmail())
                .loginId(merchant.getLoginId())  // 추가
                .status(merchant.getStatus())
                .referrerId(merchant.getReferrerId())
                .terminalId(merchant.getTerminalId())
                .createdAt(merchant.getCreatedAt())
                .applyDate(merchant.getApplyDate())
                .approveDate(merchant.getApproveDate())
                .categoryId(merchant.getCategoryId())  // 추가
                .categories(categories)
                .build();
    }
}