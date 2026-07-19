package org.best.backspringboot.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.commonDTO.PageResponse;
import org.best.backspringboot.payment.dto.payment.PaymentCreateDto;
import org.best.backspringboot.payment.dto.payment.PaymentResponseDto;
import org.best.backspringboot.payment.dto.payment.PaymentSearchDto;
import org.best.backspringboot.payment.service.PaymentService;
import org.best.backspringboot.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "결제", description = "결제 관련 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "결제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 성공"),
            @ApiResponse(responseCode = "400", description = "포인트 부족 또는 유효성 오류", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "카드 또는 가맹점 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MERCHANT')")
    @PostMapping
    public ResponseEntity<PaymentResponseDto> pay(@Valid @RequestBody PaymentCreateDto dto) {
        return ResponseEntity.ok(paymentService.pay(dto));
    }

    @Operation(summary = "결제 내역 조회 (페이징 + 검색)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "결제 내역 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getById(@PathVariable Long paymentId, @RequestParam String transmissionDate) {
        return ResponseEntity.ok(paymentService.getById(paymentId, transmissionDate));
    }

    @Operation(summary = "결제 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "400", description = "취소 기간 초과 또는 이미 취소된 결제", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "결제 내역 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MERCHANT')")
    @PatchMapping("/{paymentId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long paymentId, @RequestParam String transmissionDate) {
        paymentService.cancel(paymentId, transmissionDate);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "결제 내역 삭제 (관리자용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "결제 내역 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> delete(@PathVariable Long paymentId, @RequestParam String transmissionDate) {
        paymentService.delete(paymentId, transmissionDate);
        return ResponseEntity.ok().build();
    }
}