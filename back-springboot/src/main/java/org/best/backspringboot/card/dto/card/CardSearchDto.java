package org.best.backspringboot.card.dto.card;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.global.commonDTO.SearchBase;

@Getter
@Setter
public class CardSearchDto extends SearchBase {
    private Long memberId;
    private String status;
    private String keyword;      // 카드번호 또는 소유자명 검색
    private Integer isPrimary;   // 1=고유카드, 0=추가카드
}