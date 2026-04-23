package org.best.backspringboot.dto.merchant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
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

    @NotBlank
    @Size(max = 20)
    private String contact;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @NotBlank
    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String homepage;

    @Size(max = 50)
    private String managerName;

    @Size(max = 100)
    private String email;

    private Long referrerId;

    @Size(max = 100)
    private String terminalId;

    private List<String> categories; // 업종 목록
}