package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.payment.PaymentCreateDto;
import org.best.backspringboot.dto.payment.PaymentResponseDto;
import org.best.backspringboot.dto.payment.PaymentSearchDto;
import org.best.backspringboot.service.PaymentService;
import org.best.backspringboot.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "결제", description = "결제 관련 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "결제")
    @PostMapping
    public ResponseEntity<PaymentResponseDto> pay(@Valid @RequestBody PaymentCreateDto dto) {
        return ResponseEntity.ok(paymentService.pay(dto));
    }

    @Operation(summary = "결제 내역 조회 (페이징 + 검색)")
    @GetMapping
    public ResponseEntity<PageResponse<PaymentResponseDto>> getAll(PaymentSearchDto searchDto,
                                                                   HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            String role  = jwtUtil.getRole(token);

            if ("MERCHANT".equals(role)) {
                Long merchantId = jwtUtil.getMerchantId(token);
                searchDto.setMerchantId(merchantId);
            }
        }

        return ResponseEntity.ok(paymentService.getAll(searchDto));
    }

    @Operation(summary = "결제 단건 조회")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getById(@PathVariable Long paymentId, @RequestParam String transmissionDate) {
        return ResponseEntity.ok(paymentService.getById(paymentId, transmissionDate));
    }

    @Operation(summary = "결제 취소")
    @PatchMapping("/{paymentId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long paymentId, @RequestParam String transmissionDate) {
        paymentService.cancel(paymentId, transmissionDate);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "결제 내역 삭제 (관리자용)")
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> delete(@PathVariable Long paymentId, @RequestParam String transmissionDate) {
        paymentService.delete(paymentId, transmissionDate);
        return ResponseEntity.ok().build();
    }
}