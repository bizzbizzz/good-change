package org.best.backspringboot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Merchant {
    private Long merchantId;
    private Long memberId;          // ✅ loginId, password 제거 → memberId 추가
    private String merchantName;
    private String representative;
    private String businessNumber;
    private String contact;
    private String address;
    private String email;
    private String status;
    private Long referrerId;
    private String terminalId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime applyDate;    // 가입신청일자
    private LocalDateTime approveDate;  // 가입승인일자
    private String ip;
}