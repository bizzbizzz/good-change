package org.best.backspringboot.dto.member;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.entity.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponseDto {
    private Long memberId;
    private String loginId;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String phone;
    private String address;
    private String email;
    private Long referrerId;
    private LocalDateTime createdAt;

    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .phone(member.getPhone())
                .address(member.getAddress())
                .email(member.getEmail())
                .referrerId(member.getReferrerId())
                .createdAt(member.getCreatedAt())
                .build();
    }
}