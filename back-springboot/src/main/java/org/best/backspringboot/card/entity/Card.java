package org.best.backspringboot.card.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Card {
    private Long cardId;
    private Long memberId;
    private Long bankId;
    private String cardNumber;
    private String cardAlias;
    private Integer isPrimary;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}