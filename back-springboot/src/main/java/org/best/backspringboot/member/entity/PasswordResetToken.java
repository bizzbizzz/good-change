package org.best.backspringboot.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PasswordResetToken {
    private Long id;
    private Long memberId;
    private String token;
    private LocalDateTime expiresAt;
    private Integer used;
    private LocalDateTime createdAt;

    @Builder
    public PasswordResetToken(Long memberId, String token, LocalDateTime expiresAt) {
        this.memberId = memberId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.used = 0;
    }
}