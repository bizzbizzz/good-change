package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.allowedip.AllowedIpCreateDto;
import org.best.backspringboot.entity.AllowedIp;
import org.best.backspringboot.service.AllowedIpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "허용 IP", description = "허용 IP 관련 API")
@RestController
@RequestMapping("/api/allowed-ips")
@RequiredArgsConstructor
public class AllowedIpController {

    private final AllowedIpService allowedIpService;

    @Operation(summary = "허용 IP 등록")
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody AllowedIpCreateDto dto) {
        allowedIpService.create(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "허용 IP 전체 조회")
    @GetMapping
    public ResponseEntity<List<AllowedIp>> getAll() {
        return ResponseEntity.ok(allowedIpService.getAll());
    }

    @Operation(summary = "허용 IP 삭제")
    @DeleteMapping("/{ipId}")
    public ResponseEntity<Void> delete(@PathVariable Long ipId) {
        allowedIpService.delete(ipId);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "가맹점 허용 IP 단건 조회")
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<AllowedIp> getByMerchantId(@PathVariable Long merchantId) {
        return allowedIpService.getByMerchantId(merchantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "가맹점 허용 IP 수정")
    @PutMapping("/merchant/{merchantId}")
    public ResponseEntity<Void> updateByMerchantId(@PathVariable Long merchantId,
                                                   @RequestBody AllowedIpCreateDto dto) {
        allowedIpService.updateByMerchantId(merchantId, dto.getIpAddress());
        return ResponseEntity.ok().build();
    }
}