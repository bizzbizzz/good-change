package org.best.backspringboot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberInquiry {

    private Long memberInquiryId;
    private String memberInquiryName;
    private String memberInquiryPhone;
    private String memberInquiryTitle;
    private String memberInquiryContent;
    private String memberInquiryStatus;
    private String memberInquiryType;      // 수혜자 / 가맹점

    private LocalDateTime createDate;    // 가입신청일자
    private LocalDateTime answeredDate;  // 가입승인일자

}
