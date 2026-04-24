package org.best.backspringboot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Merchant {
    private Long merchantId;
    private String loginId;
    private String password;
    private String merchantName;
    private String representative;
    private String businessNumber;
    private String contact;
    private String address;
    private String email;
    private String status;          // ✅ 추가
    private Long referrerId;
    private String terminalId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}