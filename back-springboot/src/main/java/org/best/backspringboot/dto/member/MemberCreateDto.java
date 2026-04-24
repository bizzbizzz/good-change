package org.best.backspringboot.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Getter
public class MemberCreateDto {
    private Long memberId;
    @NotBlank
    @Size(max = 50)
    private String loginId;

    @NotBlank
    @Size(max = 255)
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
    @Size(max = 20)
    private String phone;

    @NotBlank
    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String email;

    private Long referrerId;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void encodePhone(PasswordEncoder passwordEncoder) {
        this.phone = passwordEncoder.encode(this.phone);
    }


}