package org.best.backspringboot.dto.member;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.SearchBase;

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