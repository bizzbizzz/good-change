package org.best.backspringboot.card.dto.card;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CardListBulkDto {
    private List<String> cardNumbers;
}