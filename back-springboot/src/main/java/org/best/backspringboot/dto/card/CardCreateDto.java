package org.best.backspringboot.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardCreateDto {

    private Long cardId;

    @NotNull
    private Long memberId;

    @NotBlank(message = "카드번호는 필수입니다.")
    @Size(min = 16, max = 16, message = "카드번호는 16자리여야 합니다.")
    private String cardNumber;

    @Size(max = 50)
    private String cardAlias;

    private Integer isPrimary;  // 1: 고유카드, 0: 추가카드
}