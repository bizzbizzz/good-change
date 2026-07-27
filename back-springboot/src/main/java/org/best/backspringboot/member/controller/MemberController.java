package org.best.backspringboot.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.global.commonDTO.PageResponse;
import org.best.backspringboot.member.dto.member.*;
import org.best.backspringboot.member.service.MemberService;
import org.best.backspringboot.merchant.service.MerchantService;
import org.best.backspringboot.global.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "멤버", description = "멤버 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final MerchantService merchantService;

    @Operation(summary = "회원 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 오류 또는 중복 아이디", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Map<String, Long>> create(@Valid @RequestBody MemberRegisterDto registerDto) {
        memberService.create(registerDto);
        return ResponseEntity.ok(Map.of("memberId", registerDto.getMember().getMemberId()));
    }

    @Operation(summary = "아이디 중복체크", description = "true = 사용가능, false = 중복")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "체크 완료 (true/false 반환)")
    })
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkLoginId(@RequestParam String loginId) {
        return ResponseEntity.ok(memberService.isLoginIdAvailable(loginId));
    }

    @Operation(summary = "로그인", description = "JWT 토큰 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 (JWT 토큰 반환)"),
            @ApiResponse(responseCode = "400", description = "아이디 또는 비밀번호 오류", content = @Content),
            @ApiResponse(responseCode = "403", description = "비활성 계정", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody MemberLoginDto dto) {
        String token = memberService.login(dto);


        // 토큰에서 정보 추출
        Map<String, Object> response = new HashMap<>();
        response.put("token",   token);
        response.put("loginId", jwtUtil.getLoginId(token));
        response.put("role",    jwtUtil.getRole(token));
        response.put("memberId",    jwtUtil.getMemberId(token));
        response.put("name",    memberService.getNameByLoginId(jwtUtil.getLoginId(token)));
        response.put("merchantName", merchantService.getMerchantNameByMemberId(jwtUtil.getMemberId(token)));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    @GetMapping("/{memberId:\\d+}")
    public ResponseEntity<MemberResponseDto> getById(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getById(memberId));
    }

    @Operation(summary = "회원 전체 조회 (페이징)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<MemberResponseDto>> getAll(MemberSearchDto searchDto) {
        return ResponseEntity.ok(memberService.getAll(searchDto));
    }

    @Operation(summary = "회원 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 오류", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    @PatchMapping("/{memberId:\\d+}")
    public ResponseEntity<Void> update(@PathVariable Long memberId,
                                       @Valid @RequestBody MemberUpdateDto dto) {
        memberService.update(memberId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 비활성화")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{memberId:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long memberId) {
        memberService.delete(memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
    })
    @DeleteMapping("/{memberId:\\d+}/withdraw")
    public ResponseEntity<Void> withdraw(
            @PathVariable Long memberId,
            @RequestBody Map<String, String> body) {
        String password = body.get("password");
        String reason   = body.get("reason");
        memberService.withdraw(memberId, password, reason);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        if (memberId != null) {
            memberService.logout(memberId);
        }
        return ResponseEntity.ok().build();
    }


    // 비밀번호 재설정 요청 (공개 — 어노테이션 X)
    @Operation(summary = "비밀번호 재설정 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발송 완료 (계정 유무와 무관)"),
            @ApiResponse(responseCode = "400", description = "이메일 형식 오류", content = @Content)
    })
    @PostMapping("/password-reset/request")
    public ResponseEntity<Map<String, String>> requestReset(@RequestBody Map<String, String> body) {
        memberService.requestPasswordReset(body.get("email"));
        // 계정 유무와 무관하게 동일 응답
        return ResponseEntity.ok(Map.of("message", "입력하신 이메일로 재설정 링크를 발송했습니다."));
    }

    // 비밀번호 재설정 확인 (공개 — 어노테이션 X)
    @Operation(summary = "비밀번호 재설정 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 완료"),
            @ApiResponse(responseCode = "400", description = "토큰 만료 또는 유효하지 않은 토큰", content = @Content)
    })
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Map<String, String>> confirmReset(@RequestBody Map<String, String> body) {
        memberService.confirmPasswordReset(body.get("token"), body.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
    }

    @Operation(summary = "수혜자 비밀번호 초기화", description = "고유카드번호 + ! 로 초기화 (ADMIN 이상)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초기화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 또는 카드 없음", content = @Content)
    })
    @PostMapping("/{memberId}/reset-password/member")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> resetPasswordForMember(@PathVariable Long memberId) {
        memberService.resetPasswordForMember(memberId);
        return ResponseEntity.ok(Map.of("message", "비밀번호가 초기화되었습니다. (카드번호 + !)"));
    }

    @Operation(summary = "가맹점 비밀번호 초기화", description = "사업자번호 + ! 로 초기화 (ADMIN 이상)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초기화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 또는 가맹점 없음", content = @Content)
    })
    @PostMapping("/{memberId}/reset-password/merchant")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> resetPasswordForMerchant(@PathVariable Long memberId) {
        memberService.resetPasswordForMerchant(memberId);
        return ResponseEntity.ok(Map.of("message", "비밀번호가 초기화되었습니다. (사업자번호 + !)"));
    }

    @Operation(summary = "회원 전체 조회 (페이징 없음)", description = "조건에 맞는 회원 전체를 한 번에 반환. 목록이 커질 수 있는 화면(예: 일괄지급 대상 선택)에서 사용")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<MemberResponseDto>> getAllWithoutPaging(MemberSearchDto searchDto) {


        return ResponseEntity.ok(memberService.getAllList(searchDto));
    }
}