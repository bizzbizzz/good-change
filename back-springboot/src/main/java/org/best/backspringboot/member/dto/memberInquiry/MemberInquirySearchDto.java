package org.best.backspringboot.member.dto.memberInquiry;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.global.commonDTO.SearchBase;

@Getter
@Setter
public class MemberInquirySearchDto extends SearchBase {
    private String name;
    private String phone;
    private String title;
    private String status;
    private String type;   // ✅ 추가
}