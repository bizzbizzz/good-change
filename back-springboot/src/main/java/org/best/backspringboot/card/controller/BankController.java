package org.best.backspringboot.card.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.card.entity.Bank;
import org.best.backspringboot.card.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "은행", description = "은행 관리 API")
@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    // ── 전체 조회 (모두 허용 - 카드등록 시 사용) ──────────
    @Operation(summary = "은행 목록 조회")
    @GetMapping
    public ResponseEntity<List<Bank>> getAll() {
        return ResponseEntity.ok(bankService.getAll());
    }

    // ── 단건 조회 ─────────────────────────────────────────
    @Operation(summary = "은행 단건 조회")
    @GetMapping("/{bankId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Bank> getById(@PathVariable Long bankId) {
        return ResponseEntity.ok(bankService.getById(bankId));
    }

    // ── 등록 ──────────────────────────────────────────────
    @Operation(summary = "은행 등록")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> create(@RequestBody Map<String, String> body) {
        bankService.create(body.get("bankName"), body.get("bankCode"));
        return ResponseEntity.ok().build();
    }

    // ── 수정 ──────────────────────────────────────────────
    @Operation(summary = "은행 수정")
    @PatchMapping("/{bankId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long bankId,
                                       @RequestBody Map<String, String> body) {
        bankService.update(bankId, body.get("bankName"), body.get("bankCode"), body.get("status"));
        return ResponseEntity.ok().build();
    }

    // ── 삭제 (INACTIVE 처리) ──────────────────────────────
    @Operation(summary = "은행 삭제 (비활성화)")
    @DeleteMapping("/{bankId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long bankId) {
        bankService.delete(bankId);
        return ResponseEntity.ok().build();
    }
}