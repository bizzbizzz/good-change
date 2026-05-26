package org.best.backspringboot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class Member {
    private Long memberId;
    private String loginId;
    private Long roleId;
    private String password;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String address;
    private String email;
    private Long referrerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String organization;
    private Long point;         // ✅ 추가 (보유포인트)
    private String status;      // 기존 있음
    private LocalDateTime applyDate;    // 가입신청일자
    private LocalDateTime approveDate;  // 가입승인일자
    private List<String> cardNumbers;
    private String detailAddress;
}