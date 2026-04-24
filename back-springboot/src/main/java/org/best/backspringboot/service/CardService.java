package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.card.CardResponseDto;
import org.best.backspringboot.entity.Card;
import org.best.backspringboot.entity.Member;
import org.best.backspringboot.mapper.CardMapper;
import org.best.backspringboot.mapper.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardMapper cardMapper;
    private final MemberMapper memberMapper;

    @Transactional(readOnly = true)
    public CardResponseDto getByCardNumber(String cardNumber) {
        Card card = cardMapper.findByCardNumber(cardNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드번호입니다."));

        Member member = memberMapper.findById(card.getMemberId())
                .orElse(null);

        return CardResponseDto.from(card, member);
    }
}