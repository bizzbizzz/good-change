package org.best.backspringboot.card.dto.card.cardHistory;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.global.commonDTO.SearchBase;

@Getter
@Setter
public class CardReissueHistorySearchDto extends SearchBase {
    private String keyword;    // 카드번호 또는 소유자명
    private String reason;     // LOST/DAMAGED/STOLEN/OTHER
}