// ── PointGrantRequestDto.java ──────────────────────────
package org.best.backspringboot.dto.pointgrant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PointGrantRequestDto {

    @NotBlank(message = "지급명을 입력해주세요.")
    private String title;           // 지급명

    @NotNull(message = "지급 포인트를 입력해주세요.")
    @Min(value = 1, message = "지급 포인트는 1 이상이어야 합니다.")
    private Long grantAmount;       // 1인당 지급 포인트

    @NotNull(message = "대상 회원을 선택해주세요.")
    private List<Long> memberIds;   // 지급 대상 member_id 목록
}
