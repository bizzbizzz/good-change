package org.best.backspringboot.member.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "관리자 권한 변경 요청")
public class AdminRoleChangeDto {

    @NotBlank(message = "권한을 선택해주세요.")
    @Pattern(regexp = "ADMIN|SUPER_ADMIN", message = "권한은 ADMIN 또는 SUPER_ADMIN 이어야 합니다.")
    @Schema(description = "변경할 권한", example = "ADMIN")
    private String role;
}
