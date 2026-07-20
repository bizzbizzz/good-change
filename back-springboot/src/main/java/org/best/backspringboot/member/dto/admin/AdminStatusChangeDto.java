package org.best.backspringboot.member.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "관리자 상태 변경 요청")
public class AdminStatusChangeDto {

    @NotBlank(message = "상태를 선택해주세요.")
    @Pattern(regexp = "ACTIVE|SUSPENDED", message = "상태는 ACTIVE 또는 SUSPENDED 이어야 합니다.")
    @Schema(description = "변경할 상태", example = "SUSPENDED")
    private String status;
}
