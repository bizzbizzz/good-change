package org.best.backspringboot.site.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.global.commonDTO.PageResponse;
import org.best.backspringboot.site.dto.banner.BannerCreateDto;
import org.best.backspringboot.site.dto.banner.BannerResponseDto;
import org.best.backspringboot.site.dto.banner.BannerSearchDto;
import org.best.backspringboot.site.dto.banner.BannerUpdateDto;
import org.best.backspringboot.site.service.BannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "배너", description = "배너 관리 API")
@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "배너 전체 조회 (검색 + 페이징)",
               description = "title: 제목 검색, useYn: Y/N(전체 생략), page/size 페이징")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping
    public ResponseEntity<PageResponse<BannerResponseDto>> getAll(
            BannerSearchDto searchDto) {
        return ResponseEntity.ok(bannerService.getAll(searchDto));
    }

    @Operation(summary = "배너 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "배너 없음", content = @Content)
    })
    @GetMapping("/{bannerId}")
    public ResponseEntity<BannerResponseDto> getById(@PathVariable Long bannerId) {
        return ResponseEntity.ok(bannerService.getById(bannerId));
    }

    @Operation(summary = "배너 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Long>> create(
            @Valid @ModelAttribute BannerCreateDto dto) throws Exception {
        Long bannerId = bannerService.create(dto);
        return ResponseEntity.ok(Map.of("bannerId", bannerId));
    }

    @Operation(summary = "배너 수정", description = "imageFile 없으면 기존 이미지 유지")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "배너 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(value = "/{bannerId}", consumes = "multipart/form-data")
    public ResponseEntity<Void> update(
            @PathVariable Long bannerId,
            @ModelAttribute BannerUpdateDto dto) throws Exception {
        bannerService.update(bannerId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "배너 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "배너 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{bannerId}")
    public ResponseEntity<Void> delete(@PathVariable Long bannerId) {
        bannerService.delete(bannerId);
        return ResponseEntity.ok().build();
    }
}
