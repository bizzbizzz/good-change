package org.best.backspringboot.member.dto.memberInquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.best.backspringboot.member.entity.MemberInquiry;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInquiryResponseDto {

    private Long memberInquiryId;
    private String memberInquiryName;
    private String memberInquiryPhone;
    private String memberInquiryTitle;
    private String memberInquiryContent;
    private String memberInquiryStatus;
    private String memberInquiryType;  // USER / MERCHANT
    private LocalDateTime createDate;
    private LocalDateTime answeredDate;

    // Entity → DTO 변환 (정적 팩토리 메서드)
    public static MemberInquiryResponseDto from(MemberInquiry entity) {
        return MemberInquiryResponseDto.builder()
                .memberInquiryId(entity.getMemberInquiryId())
                .memberInquiryName(entity.getMemberInquiryName())
                .memberInquiryPhone(entity.getMemberInquiryPhone())
                .memberInquiryTitle(entity.getMemberInquiryTitle())
                .memberInquiryContent(entity.getMemberInquiryContent())
                .memberInquiryStatus(entity.getMemberInquiryStatus())
                .memberInquiryType(entity.getMemberInquiryType()) 
                .createDate(entity.getCreateDate())
                .answeredDate(entity.getAnsweredDate())
                .build();
    }
}