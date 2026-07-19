package org.best.backspringboot.member.dto.member;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.commonDTO.SearchBase;

@Getter
@Setter
public class MemberSearchDto extends SearchBase {
    private String name;
    private String birthDate;
    private String cardNumber;
    private String organization;
    private String status;
    private String keyword;
}