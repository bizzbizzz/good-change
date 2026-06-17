package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.settlement.SettlementResponseDto;
import org.best.backspringboot.dto.settlement.SettlementSearchDto;
import org.best.backspringboot.dto.settlement.SettlementSummaryDto;
import org.best.backspringboot.service.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "정산", description = "정산 관련 API")
@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @Operation(summary = "정산 내역 조회 (페이징 + 검색)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MERCHANT')")
    @GetMapping
    public ResponseEntity<PageResponse<SettlementResponseDto>> getAll(SettlementSearchDto dto) {
        return ResponseEntity.ok(settlementService.getAll(dto));
    }

    @Operation(summary = "정산 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "정산 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MERCHANT')")
    @GetMapping("/{settlementId}")
    public ResponseEntity<SettlementResponseDto> getById(@PathVariable Long settlementId) {
        return ResponseEntity.ok(settlementService.getById(settlementId));
    }

    // ✅ 당월/전월 정산금액 요약 조회
    @Operation(summary = "당월/전월 정산금액 요약 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MERCHANT')")
    @GetMapping("/summary")
    public ResponseEntity<SettlementSummaryDto> getSummary(@RequestParam Long merchantId) {
        return ResponseEntity.ok(settlementService.getSummary(merchantId));
    }

    @Operation(summary = "정산 상태 월단위 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 상태값", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "정산 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping("/status")
    public ResponseEntity<Void> updateStatusByMonth(
            @RequestParam Long merchantId,
            @RequestParam String settlementMonth,
            @RequestParam String status) {
        settlementService.updateStatus(merchantId, settlementMonth, status);
        return ResponseEntity.ok().build();
    }
}