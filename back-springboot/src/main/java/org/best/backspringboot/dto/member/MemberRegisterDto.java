package org.best.backspringboot.dto.member;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.card.CardCreateDto;

import java.util.List;

@Getter
@Setter
public class MemberRegisterDto {
    private MemberCreateDto member;
    private List<CardCreateDto> cards;  // card → cards 리스트로
}