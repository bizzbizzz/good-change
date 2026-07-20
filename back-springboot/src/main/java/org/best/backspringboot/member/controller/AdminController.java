package org.best.backspringboot.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.commonDTO.PageResponse;
import org.best.backspringboot.member.dto.admin.AdminResponseDto;
import org.best.backspringboot.member.dto.admin.AdminRoleChangeDto;
import org.best.backspringboot.member.dto.admin.AdminSearchDto;
import org.best.backspringboot.member.dto.admin.AdminStatusChangeDto;
import org.best.backspringboot.member.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 관리자(ADMIN) 계정 전용 API
 * - member 테이블 중 role_id = ADMIN 인 계정만 대상으로 함 (SUPER_ADMIN은 이 API로 절대 조회/관리되지 않음)
 * - 목록조회/권한변경/계정정지(정지해제)/비밀번호 랜덤초기화/삭제는 전부 SUPER_ADMIN만 가능
 */

@Tag(name = "관리자 계정 관리", description = "관리자(ADMIN) 계정 관리 API - SUPER_ADMIN 전용")
@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "관리자 목록 조회 (페이징)", description = "role_id = ADMIN 인 계정만 조회됨. SUPER_ADMIN은 절대 포함되지 않음")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<AdminResponseDto>> getAll(AdminSearchDto searchDto) {
        return ResponseEntity.ok(adminService.getAllAdmin(searchDto));
    }

    @Operation(summary = "관리자 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "관리자 없음", content = @Content)
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{adminId:\\d+}")
    public ResponseEntity<AdminResponseDto> getById(@PathVariable Long adminId) {
        return ResponseEntity.ok(adminService.getAdminById(adminId));
    }

    @Operation(summary = "관리자 권한 변경", description = "ADMIN <-> SUPER_ADMIN 변경. SUPER_ADMIN으로 변경 시 이후 이 API 목록에서 제외됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 오류", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "관리자 없음", content = @Content)
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{adminId:\\d+}/role")
    public ResponseEntity<Void> changeRole(@PathVariable Long adminId,
                                           @Valid @RequestBody AdminRoleChangeDto dto) {
        adminService.updateRole(adminId, dto.getRole());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관리자 상태(정지/정상) 변경", description = "정지된 관리자는 로그인이 차단됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 오류", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "관리자 없음", content = @Content)
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{adminId:\\d+}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long adminId,
                                             @Valid @RequestBody AdminStatusChangeDto dto) {
        adminService.updateStatus(adminId, dto.getStatus());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관리자 비밀번호 랜덤 초기화", description = "영문+숫자+특수문자 조합의 임시 비밀번호를 생성하여 저장하고, 생성된 값을 1회 응답으로 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초기화 성공 (임시 비밀번호 반환)"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "관리자 없음", content = @Content)
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/{adminId:\\d+}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long adminId) {
        String tempPassword = adminService.resetPassword(adminId);
        return ResponseEntity.ok(Map.of(
                "message", "비밀번호가 초기화되었습니다.",
                "tempPassword", tempPassword
        ));
    }

    @Operation(summary = "관리자 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "관리자 없음", content = @Content)
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{adminId:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.ok().build();
    }
}
