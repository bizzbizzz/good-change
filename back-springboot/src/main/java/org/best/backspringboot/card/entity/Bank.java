package org.best.backspringboot.card.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Bank {
    private Long bankId;
    private String bankName;
    private String bankCode;
    private String status;
    private LocalDateTime createdAt;
}
