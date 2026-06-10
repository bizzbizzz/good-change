package org.best.backspringboot.dto.cardHistory;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.SearchBase;

@Getter
@Setter
public class CardReissueHistorySearchDto extends SearchBase {
    private String keyword;    // 카드번호 또는 소유자명
    private String reason;     // LOST/DAMAGED/STOLEN/OTHER
}