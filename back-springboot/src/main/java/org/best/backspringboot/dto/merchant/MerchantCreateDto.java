package org.best.backspringboot.dto.merchant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Getter
@Setter
public class MerchantCreateDto {

    private Long merchantId;

    @NotBlank
    @Size(max = 50)
    private String loginId;

    @NotBlank
    @Size(max = 255)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String merchantName;

    @NotBlank
    @Size(max = 50)
    private String representative;

    @NotBlank
    @Size(max = 20)
    private String businessNumber;

    @Size(max = 20)
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "연락처 형식이 올바르지 않습니다.")
    private String contact;

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String email;

    private Long referrerId;

    @Size(max = 100)
    private String terminalId;

    private List<String> categories;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}