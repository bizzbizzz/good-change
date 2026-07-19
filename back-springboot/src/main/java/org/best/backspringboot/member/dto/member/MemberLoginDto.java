package org.best.backspringboot.member.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberLoginDto {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;
    private String role;  // 추가
}