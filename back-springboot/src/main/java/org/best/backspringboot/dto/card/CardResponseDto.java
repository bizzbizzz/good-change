package org.best.backspringboot.dto.card;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.entity.Card;
import org.best.backspringboot.entity.Member;

@Getter
@Builder
public class CardResponseDto {
    private Long cardId;
    private Long memberId;
    private String cardNumber;
    private String cardAlias;
    private Integer isPrimary;
    private String status;
    private String memberName;   // 회원 이름
    private Long point;          // 회원 보유포인트

    public static CardResponseDto from(Card card, Member member) {
        return CardResponseDto.builder()
                .cardId(card.getCardId())
                .memberId(card.getMemberId())
                .cardNumber(card.getCardNumber())
                .cardAlias(card.getCardAlias())
                .isPrimary(card.getIsPrimary())
                .status(card.getStatus())
                .memberName(member != null ? member.getName() : null)
                .point(member != null ? member.getPoint() : null)
                .build();
    }
}