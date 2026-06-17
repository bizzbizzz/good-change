package org.best.backspringboot.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor  // 추가
@AllArgsConstructor // @Builder와 함께 쓰려면 이것도 추가
public class CardCreateDto {

    private Long cardId;
    private Long bankId;

    @NotNull
    private Long memberId;

    @NotBlank(message = "카드번호는 필수입니다.")
    @Size(min = 16, max = 16, message = "카드번호는 16자리여야 합니다.")
    private String cardNumber;

    @Size(max = 50)
    private String cardAlias;

    private Integer isPrimary;  // 회원가입 시 첫 카드는 고유카드(1)
}