package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.allowedip.AllowedIpCreateDto;
import org.best.backspringboot.entity.AllowedIp;
import org.best.backspringboot.service.AllowedIpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "허용 IP", description = "허용 IP 관련 API")
@RestController
@RequestMapping("/api/allowed-ips")
@RequiredArgsConstructor
public class AllowedIpController {

    private final AllowedIpService allowedIpService;

    @Operation(summary = "허용 IP 등록", description = "새로운 허용 IP를 등록합니다. ADMIN 또는 SUPER_ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody AllowedIpCreateDto dto) {
        allowedIpService.create(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "허용 IP 전체 조회", description = "등록된 모든 허용 IP 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<List<AllowedIp>> getAll() {
        return ResponseEntity.ok(allowedIpService.getAll());
    }

    @Operation(summary = "허용 IP 삭제", description = "IP ID로 허용 IP를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{ipId}")
    public ResponseEntity<Void> delete(@PathVariable Long ipId) {
        allowedIpService.delete(ipId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "가맹점 허용 IP 단건 조회", description = "가맹점 ID로 해당 가맹점의 허용 IP를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 가맹점의 허용 IP 없음", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<AllowedIp> getByMerchantId(@PathVariable Long merchantId) {
        return allowedIpService.getByMerchantId(merchantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "가맹점 허용 IP 수정", description = "가맹점 ID로 해당 가맹점의 허용 IP를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/merchant/{merchantId}")
    public ResponseEntity<Void> updateByMerchantId(@PathVariable Long merchantId,
                                                   @RequestBody AllowedIpCreateDto dto) {
        allowedIpService.updateByMerchantId(merchantId, dto.getIpAddress());
        return ResponseEntity.ok().build();
    }
}