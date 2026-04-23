package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.SearchBase;
import org.best.backspringboot.dto.merchant.MerchantCreateDto;
import org.best.backspringboot.dto.merchant.MerchantResponseDto;
import org.best.backspringboot.dto.merchant.MerchantUpdateDto;
import org.best.backspringboot.service.MerchantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "가맹점", description = "가맹점 관련 API")
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @Operation(summary = "가맹점 등록")
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody MerchantCreateDto dto) {
        merchantService.create(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "가맹점 단건 조회")
    @GetMapping("/{merchantId}")
    public ResponseEntity<MerchantResponseDto> getById(@PathVariable Long merchantId) {
        return ResponseEntity.ok(merchantService.getById(merchantId));
    }

    @Operation(summary = "가맹점 전체 조회 (페이징)")
    @GetMapping
    public ResponseEntity<PageResponse<MerchantResponseDto>> getAll(SearchBase searchBase) {
        return ResponseEntity.ok(merchantService.getAll(searchBase));
    }

    @Operation(summary = "가맹점 수정")
    @PatchMapping("/{merchantId}")
    public ResponseEntity<Void> update(@PathVariable Long merchantId,
                                       @Valid @RequestBody MerchantUpdateDto dto) {
        merchantService.update(merchantId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "가맹점 삭제")
    @DeleteMapping("/{merchantId}")
    public ResponseEntity<Void> delete(@PathVariable Long merchantId) {
        merchantService.delete(merchantId);
        return ResponseEntity.ok().build();
    }
}