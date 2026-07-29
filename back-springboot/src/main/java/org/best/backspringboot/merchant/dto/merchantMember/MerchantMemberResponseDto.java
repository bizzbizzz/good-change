package org.best.backspringboot.merchant.dto.merchantMember;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.member.entity.Member;
import org.best.backspringboot.member.entity.Role;
import org.best.backspringboot.merchant.entity.Merchant;
import org.best.backspringboot.merchant.entity.MerchantMember;

import java.time.LocalDateTime;

@Getter
@Builder
public class MerchantMemberResponseDto {
    private Long   memberId;
    private Long   merchantId;
    private String merchantName;
    private String name;
    private String loginId;
    private String role;        // OWNER, STAFF
    private String contact;
    private String email;
    private String status;
    private LocalDateTime createdAt;

    public static MerchantMemberResponseDto from(MerchantMember mm, Merchant merchant, Member member, Role role) {
        return MerchantMemberResponseDto.builder()
                .memberId(member.getMemberId())
                .merchantId(merchant.getMerchantId())
                .merchantName(merchant.getMerchantName())
                .name(member.getName())
                .loginId(member.getLoginId())
                .role(role.getRoleName())
                .contact(member.getEmail())
                .email(member.getEmail())
                .status(member.getStatus())
                .createdAt(mm.getCreatedAt())
                .build();
    }
}