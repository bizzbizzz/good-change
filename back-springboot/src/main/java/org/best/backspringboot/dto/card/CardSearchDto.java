package org.best.backspringboot.dto.card;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.SearchBase;

@Getter
@Setter
public class CardSearchDto extends SearchBase {
    private Long memberId;
    private String status;
}