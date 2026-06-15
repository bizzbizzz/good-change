package org.best.backspringboot.dto.member;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.entity.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MemberResponseDto {
    private Long memberId;
    private String loginId;
    private String name;
    private LocalDate birthDate;
    private Long roleId;
    private String gender;
    private String address;
    private String email;
    private Long referrerId;
    private String status;
    private LocalDateTime createdAt;
    private String organization;
    private Long point;
    private List<String> cardNumbers;
    private LocalDateTime applyDate;
    private LocalDateTime approveDate;
    private String detailAddress;
    private String memo;


    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .address(member.getAddress())
                .email(member.getEmail())
                .referrerId(member.getReferrerId())
                .roleId(member.getRoleId())       // ✅ 추가
                .point(member.getPoint())          // ✅ 추가
                .status(member.getStatus())        // ✅ 추가
                .applyDate(member.getApplyDate())
                .detailAddress(member.getDetailAddress())
                .approveDate(member.getApproveDate())
                .cardNumbers(member.getCardNumbers())
                .organization(member.getOrganization())
                .createdAt(member.getCreatedAt())
                .memo(member.getMemo())
                .build();
    }
}