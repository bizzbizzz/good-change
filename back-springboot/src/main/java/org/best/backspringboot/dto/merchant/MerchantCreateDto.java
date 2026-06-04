package org.best.backspringboot.dto.merchant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MerchantCreateDto {

    private Long merchantId;
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

    @Size(max = 20)
    private String contact2;

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String email;

    private Long referrerId;

    @Size(max = 100)
    private String terminalId;

    private Long categoryId;  // categories 대신

    private LocalDateTime applyDate;
    private LocalDateTime approveDate;

    public String getTerminalId() {
        return (terminalId != null && terminalId.isBlank()) ? null : terminalId;
    }
}