package org.best.backspringboot.dto.memberInquiry;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.SearchBase;

@Getter
@Setter
public class MemberInquirySearchDto extends SearchBase {
    private String name;
    private String phone;
    private String title;
    private String status;
}