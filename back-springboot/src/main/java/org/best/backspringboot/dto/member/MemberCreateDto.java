package org.best.backspringboot.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MemberCreateDto {
    private Long memberId;
    @NotBlank
    @Size(max = 50)
    private String loginId;
    private Long roleId;

    @NotBlank
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotNull
    private LocalDate birthDate;

    @NotBlank
    @Pattern(regexp = "MALE|FEMALE")
    private String gender;

    @NotBlank
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "휴대폰번호 형식이 올바르지 않습니다. (예: 01012341234)")
    @Size(max = 20)
    private String phone;

    @NotBlank
    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String email;

    private Long referrerId;

    private LocalDateTime applyDate;
    private LocalDateTime approveDate;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void encodePhone(PasswordEncoder passwordEncoder) {
        this.phone = passwordEncoder.encode(this.phone);
    }
}