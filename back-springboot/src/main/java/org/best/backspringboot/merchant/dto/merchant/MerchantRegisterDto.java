package org.best.backspringboot.merchant.dto.merchant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.member.dto.member.MemberRegisterDto;

@Setter
@Getter
public class MerchantRegisterDto {
    @Valid
    private MemberRegisterDto member;
    @Valid
    private MerchantCreateDto merchant;
    @NotBlank
    private String ipAddress;
}