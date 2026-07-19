package org.best.backspringboot.member.dto.memberInquiry;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInquiryRequestDto {

    private String memberInquiryName;
    private String memberInquiryPhone;
    private String memberInquiryTitle;
    private String memberInquiryContent;
    private String memberInquiryType;  // USER / MERCHANT

}