package org.best.backspringboot.merchant.dto.merchant;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MerchantWithdrawDto {
    @NotBlank
    private String password;
    private String reason;
}