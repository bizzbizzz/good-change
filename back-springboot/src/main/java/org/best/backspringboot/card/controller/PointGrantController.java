package org.best.backspringboot.card.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.global.commonDTO.PageResponse;
import org.best.backspringboot.card.dto.point.PointGrantLogResponseDto;
import org.best.backspringboot.card.dto.point.PointGrantLogSearchDto;
import org.best.backspringboot.card.dto.point.PointGrantLogSummaryDto;
import org.best.backspringboot.card.dto.point.PointGrantRequestDto;
import org.best.backspringboot.card.service.PointGrantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "포인트 일괄지급", description = "포인트 일괄지급 및 로그 API")
@RestController
@RequestMapping("/api/point-grants")
@RequiredArgsConstructor
public class PointGrantController {

    private final PointGrantService pointGrantService;

    @Operation(summary = "포인트 일괄지급",
               description = "memberIds 목록에 포인트를 일괄 지급하고 로그를 남깁니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지급 성공 (성공/실패 결과 목록 반환)"),
            @ApiResponse(responseCode = "400", description = "유효성 오류", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<List<PointGrantLogResponseDto>> grant(
            @Valid @RequestBody PointGrantRequestDto dto,
            HttpServletRequest request) {

        Long adminMemberId = (Long) request.getAttribute("memberId");
        List<PointGrantLogResponseDto> result = pointGrantService.grantPoints(dto, adminMemberId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "지급 이력 목록 (배치 단위 집계)",
               description = "같은 일괄지급끼리 묶어서 집계한 목록입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/summary")
    public ResponseEntity<PageResponse<PointGrantLogSummaryDto>> getSummaryList(
            PointGrantLogSearchDto searchDto) {
        return ResponseEntity.ok(pointGrantService.getSummaryList(searchDto));
    }

    @Operation(summary = "배치 단위 상세 로그 조회",
               description = "특정 grantId(배치)의 개별 수혜자 지급 결과를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "배치 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/summary/{grantId}")
    public ResponseEntity<List<PointGrantLogResponseDto>> getDetailByGrantId(
            @PathVariable String grantId) {
        return ResponseEntity.ok(pointGrantService.getDetailByGrantId(grantId));
    }

    @Operation(summary = "개별 로그 전체 조회 (검색+페이징)",
               description = "title, memberName, status, 날짜 범위로 검색 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/logs")
    public ResponseEntity<PageResponse<PointGrantLogResponseDto>> getLogList(
            PointGrantLogSearchDto searchDto) {
        return ResponseEntity.ok(pointGrantService.getLogList(searchDto));
    }
}
