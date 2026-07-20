package org.best.backspringboot.member.dto.admin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.best.backspringboot.member.entity.Member;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 응답 DTO")
public class AdminResponseDto {

    @Schema(description = "회원(관리자) PK")
    private Long id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "로그인 아이디")
    private String loginId;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "권한 (ADMIN/SUPER_ADMIN)")
    private String role;

    @Schema(description = "상태 (ACTIVE/SUSPENDED 등)")
    private String status;

    @Schema(description = "가입일")
    private LocalDateTime regDate;

    @Schema(description = "가입승인일")
    private LocalDateTime approveDate;

    /**
     * Member 엔티티(roleId 로 관리자 여부/등급을 구분) -> AdminResponseDto 변환
     * role_id 코드: 1 = ADMIN, 2 = USER(수혜자), 3 = MERCHANT(가맹점), 9999 = SUPER_ADMIN
     */
    public static AdminResponseDto from(Member member) {
        return new AdminResponseDto(
                member.getMemberId(),
                member.getName(),
                member.getLoginId(),
                member.getEmail(),
                member.getRoleId() != null && member.getRoleId() == 9999L ? "SUPER_ADMIN" : "ADMIN",
                member.getStatus(),
                member.getCreatedAt(),
                member.getApproveDate()
        );
    }
}
