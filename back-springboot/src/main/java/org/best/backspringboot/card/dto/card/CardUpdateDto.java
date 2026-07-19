package org.best.backspringboot.card.dto.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardUpdateDto {
    private String cardNumber;
    private String cardAlias;
    private String status;
    private Integer isPrimary;   // 추가 (없으면)
}