package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.SearchBase;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.member.*;
import org.best.backspringboot.service.MemberService;
import org.best.backspringboot.service.MerchantService;
import org.best.backspringboot.util.JwtUtil;
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
    @PostMapping
    public ResponseEntity<Map<String, Long>> create(@Valid @RequestBody MemberRegisterDto registerDto) {
        memberService.create(registerDto);
        return ResponseEntity.ok(Map.of("memberId", registerDto.getMember().getMemberId()));
    }

    @Operation(summary = "아이디 중복체크", description = "true = 사용가능, false = 중복")
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkLoginId(@RequestParam String loginId) {
        return ResponseEntity.ok(memberService.isLoginIdAvailable(loginId));
    }

    @Operation(summary = "로그인", description = "JWT 토큰 반환")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody MemberLoginDto dto) {
        String token = memberService.login(dto);


        // 토큰에서 정보 추출
        Map<String, Object> response = new HashMap<>();
        response.put("token",   token);
        response.put("loginId", jwtUtil.getLoginId(token));
        response.put("role",    jwtUtil.getRole(token));
        response.put("name",    memberService.getNameByLoginId(jwtUtil.getLoginId(token)));
        response.put("merchantName", merchantService.getMerchantNameByMemberId(jwtUtil.getMemberId(token)));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 단건 조회")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    @GetMapping("/{memberId:\\d+}")
    public ResponseEntity<MemberResponseDto> getById(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getById(memberId));
    }

    @Operation(summary = "회원 전체 조회 (페이징)")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<MemberResponseDto>> getAll(MemberSearchDto searchDto) {
        return ResponseEntity.ok(memberService.getAll(searchDto));
    }

    @Operation(summary = "회원 수정")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    @PatchMapping("/{memberId:\\d+}")
    public ResponseEntity<Void> update(@PathVariable Long memberId,
                                       @Valid @RequestBody MemberUpdateDto dto) {
        memberService.update(memberId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 삭제")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{memberId:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long memberId) {
        memberService.delete(memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃")
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
    @PostMapping("/password-reset/request")
    public ResponseEntity<Map<String, String>> requestReset(@RequestBody Map<String, String> body) {
        memberService.requestPasswordReset(body.get("email"));
        // 계정 유무와 무관하게 동일 응답
        return ResponseEntity.ok(Map.of("message", "입력하신 이메일로 재설정 링크를 발송했습니다."));
    }

    // 비밀번호 재설정 확인 (공개 — 어노테이션 X)
    @Operation(summary = "비밀번호 재설정 확인")
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Map<String, String>> confirmReset(@RequestBody Map<String, String> body) {
        memberService.confirmPasswordReset(body.get("token"), body.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
    }
}