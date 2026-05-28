package org.best.backspringboot.dto.merchant;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MerchantUpdateDto {

    private String loginId;   // 추가
    private String password;  // 추가

    @Size(max = 255)
    private String detailAddress;  // 추가

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

    private List<String> categories;

    public String getTerminalId() {
        return (terminalId != null && terminalId.isBlank()) ? null : terminalId;
    }
}