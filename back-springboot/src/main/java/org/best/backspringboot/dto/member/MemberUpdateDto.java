package org.best.backspringboot.dto.member;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberUpdateDto {

    @Size(max = 50)
    private String name;

    @Pattern(regexp = "MALE|FEMALE")
    private String gender;          // 추가

    private LocalDate birthDate;    // 추가

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String email;

    @Size(max = 100)
    private String organization;

    private String status;          // 추가 - 관리자가 활성/비활성 변경

    @Size(min = 8, max = 20)
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>\\/?]).{8,20}$"
    )
    private String password;        // 추가 - 비밀번호 변경

    private Long referrerId;        // 추가 - 추천인 변경

    @Min(0)
    @Max(999999999)
    private Long point;
}