package org.best.backspringboot.dto.member;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberUpdateDto {

    @Size(max = 50)
    private String name;

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String email;
}