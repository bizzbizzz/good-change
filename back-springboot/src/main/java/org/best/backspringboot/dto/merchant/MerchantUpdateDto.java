package org.best.backspringboot.dto.merchant;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MerchantUpdateDto {

    @Size(max = 100)
    private String merchantName;

    @Size(max = 50)
    private String representative;

    @Size(max = 20)
    private String contact;

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String email;

    @Size(max = 100)
    private String terminalId;
}