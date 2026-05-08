package org.best.backspringboot.dto.merchant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MerchantCreateDto {

    private Long merchantId;

    @NotNull
    private Long memberId;          // ✅ 가맹점 관리자 member_id

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
    private String contact;

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String email;

    private Long referrerId;

    @Size(max = 100)
    private String terminalId;

    private String ip;
    private LocalDateTime applyDate;
    private LocalDateTime approveDate;
}