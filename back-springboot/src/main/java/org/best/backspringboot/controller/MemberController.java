package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.SearchBase;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.member.*;
import org.best.backspringboot.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Tag(name = "멤버", description = "멤버 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody MemberLoginDto dto) {
        String token = memberService.login(dto);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Operation(summary = "회원 단건 조회")
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getById(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getById(memberId));
    }

    @Operation(summary = "회원 전체 조회 (페이징)")
    @GetMapping
    public ResponseEntity<PageResponse<MemberResponseDto>> getAll(MemberSearchDto searchDto) {
        return ResponseEntity.ok(memberService.getAll(searchDto));
    }

    @Operation(summary = "회원 수정")
    @PatchMapping("/{loginId}")
    public ResponseEntity<Void> update(@PathVariable String loginId,
                                       @Valid @RequestBody MemberUpdateDto dto) {
        memberService.update(loginId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 삭제")
    @DeleteMapping("/{loginId}")
    public ResponseEntity<Void> delete(@PathVariable String loginId) {
        memberService.delete(loginId);
        return ResponseEntity.ok().build();
    }
}