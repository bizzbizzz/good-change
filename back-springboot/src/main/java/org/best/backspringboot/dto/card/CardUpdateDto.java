package org.best.backspringboot.dto.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardUpdateDto {
    private String cardNumber;
    private String cardAlias;
    private String status;
}