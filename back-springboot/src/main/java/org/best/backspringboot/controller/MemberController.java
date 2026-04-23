package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.member.MemberCreateDto;
import org.best.backspringboot.dto.member.MemberLoginDto;
import org.best.backspringboot.dto.member.MemberResponseDto;
import org.best.backspringboot.dto.member.MemberUpdateDto;
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
    public ResponseEntity<Void> create(@Valid @RequestBody MemberCreateDto dto) {
        memberService.create(dto);
        return ResponseEntity.ok().build();
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

    @Operation(summary = "회원 전체 조회")
    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> getAll() {
        return ResponseEntity.ok(memberService.getAll());
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