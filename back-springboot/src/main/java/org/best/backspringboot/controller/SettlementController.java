package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.settlement.SettlementResponseDto;
import org.best.backspringboot.dto.settlement.SettlementSearchDto;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<SettlementResponseDto>> getAll(SettlementSearchDto dto) {
        return ResponseEntity.ok(settlementService.getAll(dto));
    }

    @Operation(summary = "정산 단건 조회")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{settlementId}")
    public ResponseEntity<SettlementResponseDto> getById(@PathVariable Long settlementId) {
        return ResponseEntity.ok(settlementService.getById(settlementId));
    }

    @Operation(summary = "정산 상태 월단위 변경")
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