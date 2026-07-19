package org.best.backspringboot.member.dto.member;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Setter
@AllArgsConstructor
public class MemberCreateDto {
    private Long memberId;
    @NotBlank
    @Size(max = 50)
    private String loginId;
    private Long roleId;

    @NotBlank
    @Size(min = 5, max = 20, message = "비밀번호는 5자 이상 20자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>\\/?]).{5,20}$",
            message = "비밀번호는 숫자, 특수문자를 포함해야 합니다."
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
    @Size(max = 255)
    private String address;

    @Builder.Default
    @Min(0)
    @Max(999999999)
    private Long point = 0L;  // 추가

    @Size(max = 100)
    private String email;

    private Long referrerId;
    @Size(max = 100)
    private String organization;

    private LocalDateTime applyDate;
    private LocalDateTime approveDate;
    private String memo;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}