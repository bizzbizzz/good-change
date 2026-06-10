package org.best.backspringboot.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CardReissueHistory {
    private Long historyId;
    private Long oldCardId;
    private String oldCardNumber;
    private Long newCardId;
    private String newCardNumber;
    private Long memberId;
    private String reason;
    private LocalDateTime createdAt;
}