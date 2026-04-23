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
    private String phone;
    private String address;
    private String homepage;
    private String managerName;
    private String email;
    private Long referrerId;
    private String terminalId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}