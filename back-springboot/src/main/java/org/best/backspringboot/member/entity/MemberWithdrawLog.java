package org.best.backspringboot.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberWithdrawLog {
    private Long   logId;
    private Long   memberId;
    private String loginId;
    private String name;
    private String reason;
    private LocalDateTime createdAt;
}