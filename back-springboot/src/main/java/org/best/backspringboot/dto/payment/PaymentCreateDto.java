package org.best.backspringboot.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentCreateDto {

    @NotBlank
    private String cardNumber;      // 카드번호 16자리

    @NotNull
    @Min(value = 1, message = "사용포인트는 1 이상이어야 합니다.")
    private Long amount;            // 사용포인트
}