package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryRequestDto;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryResponseDto;
import org.best.backspringboot.dto.memberInquiry.MemberInquirySearchDto;
import org.best.backspringboot.service.MemberInquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원가입 문의", description = "회원가입 문의 관련 API")
@RestController
@RequestMapping("/api/inquiry")
@RequiredArgsConstructor
public class MemberInquiryController {

    private final MemberInquiryService memberInquiryService;

    @Operation(summary = "문의 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> insertInquiry(@RequestBody MemberInquiryRequestDto dto) {
        memberInquiryService.insertInquiry(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "문의 전체 조회 (페이징)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping
    public ResponseEntity<PageResponse<MemberInquiryResponseDto>> getAllInquiry(MemberInquirySearchDto searchDto) {
        return ResponseEntity.ok(memberInquiryService.getAllInquiry(searchDto));
    }

    @Operation(summary = "문의 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "문의 없음", content = @Content)
    })
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<MemberInquiryResponseDto> getInquiryById(@PathVariable Long id) {
        return ResponseEntity.ok(memberInquiryService.getInquiryById(id));
    }

    @Operation(summary = "문의 완료 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "문의 없음", content = @Content)
    })
    @PatchMapping("/{id:\\d+}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id) {
        memberInquiryService.updateStatus(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "문의 일괄 완료처리")
    @PatchMapping("/status/bulk")
    public ResponseEntity<Void> updateStatusByIds(@RequestBody List<Long> ids) {
        memberInquiryService.updateStatusByIds(ids);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "문의 일괄 대기처리")
    @PatchMapping("/status/bulk-wait")
    public ResponseEntity<Void> updateStatusToWaitByIds(@RequestBody List<Long> ids) {
        memberInquiryService.updateStatusToWaitByIds(ids);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "문의 일괄 삭제")
    @DeleteMapping
    public ResponseEntity<Void> deleteByIds(@RequestBody List<Long> ids) {
        memberInquiryService.deleteByIds(ids);
        return ResponseEntity.ok().build();
    }
}