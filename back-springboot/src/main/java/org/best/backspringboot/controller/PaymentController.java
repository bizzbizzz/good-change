package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.payment.PaymentCreateDto;
import org.best.backspringboot.dto.payment.PaymentResponseDto;
import org.best.backspringboot.dto.payment.PaymentSearchDto;
import org.best.backspringboot.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "결제", description = "결제 관련 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제")
    @PostMapping
    public ResponseEntity<PaymentResponseDto> pay(@Valid @RequestBody PaymentCreateDto dto) {
        return ResponseEntity.ok(paymentService.pay(dto));
    }

    @Operation(summary = "결제 내역 조회 (페이징 + 검색)")
    @GetMapping
    public ResponseEntity<PageResponse<PaymentResponseDto>> getAll(PaymentSearchDto dto) {
        return ResponseEntity.ok(paymentService.getAll(dto));
    }

    @Operation(summary = "결제 단건 조회")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getById(paymentId));
    }

    @Operation(summary = "결제 취소")
    @PatchMapping("/{paymentId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long paymentId) {
        paymentService.cancel(paymentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "결제 내역 삭제 (관리자용)")
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> delete(@PathVariable Long paymentId) {
        paymentService.delete(paymentId);
        return ResponseEntity.ok().build();
    }
}