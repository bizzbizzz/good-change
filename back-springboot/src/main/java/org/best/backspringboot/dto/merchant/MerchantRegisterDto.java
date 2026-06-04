package org.best.backspringboot.dto.merchant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.member.MemberCreateDto;
import org.best.backspringboot.dto.member.MemberRegisterDto;

import java.util.List;

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