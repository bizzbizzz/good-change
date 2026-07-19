package org.best.backspringboot.card.dto.card.cardHistory;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CardReissueHistoryResponse {
    private Long historyId;
    private Long oldCardId;
    private String oldCardNumber;
    private Long newCardId;
    private String newCardNumber;
    private Long memberId;
    private String reason;
    private LocalDateTime createdAt;
    private String memberName;    // JOIN으로 채워짐 (DB 컬럼 아님)
}