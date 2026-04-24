package org.best.backspringboot.dto.merchant;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MerchantUpdateDto {

    @Size(max = 100)
    private String merchantName;

    @Size(max = 50)
    private String representative;

    @Size(max = 20)
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "연락처 형식이 올바르지 않습니다.")
    private String contact;

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String email;

    @Size(max = 100)
    private String terminalId;

    @Size(max = 255)
    private String password;


}