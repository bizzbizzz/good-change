package org.best.backspringboot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String phone;
    private String address;
    private String email;
    private Long referrerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}