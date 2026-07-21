package org.best.backspringboot.merchant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.global.commonDTO.PageResponse;
import org.best.backspringboot.merchant.dto.merchant.*;
import org.best.backspringboot.merchant.service.MerchantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "가맹점", description = "가맹점 관련 API")
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @Operation(summary = "가맹점 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "중복 사업자번호 또는 유효성 오류", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody MerchantCreateDto dto) {
        merchantService.create(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "가맹점 + 회원 통합 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "중복 아이디/사업자번호 또는 유효성 오류", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody MerchantRegisterDto dto) {
        merchantService.createWithMember(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "가맹점 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "가맹점 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MERCHANT')")
    @GetMapping("/{merchantId}")
    public ResponseEntity<MerchantResponseDto> getById(@PathVariable Long merchantId) {
        return ResponseEntity.ok(merchantService.getById(merchantId));
    }

    @Operation(summary = "member_id로 가맹점 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "가맹점 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MERCHANT')")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<MerchantResponseDto> getByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(merchantService.getByMemberId(memberId));
    }

    @Operation(summary = "가맹점 전체 조회 (페이징)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<PageResponse<MerchantResponseDto>> getAll(MerchantSearchDto searchBase) {
        return ResponseEntity.ok(merchantService.getAll(searchBase));
    }

    @Operation(summary = "가맹점 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 오류", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "가맹점 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MERCHANT')")
    @PatchMapping("/{merchantId}")
    public ResponseEntity<Void> update(@PathVariable Long merchantId,
                                       @Valid @RequestBody MerchantUpdateDto dto) {
        merchantService.update(merchantId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "가맹점 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "가맹점 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{merchantId}")
    public ResponseEntity<Void> delete(@PathVariable Long merchantId) {
        merchantService.delete(merchantId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카테고리 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(merchantService.getCategories());
    }
}