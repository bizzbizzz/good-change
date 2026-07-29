package org.best.backspringboot.merchant.dto.merchantMember;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MerchantAddMemberDto {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotNull
    private Long   roleId;
    private String name;      // ✅ 추가
    private String email;     // ✅ 추가
}