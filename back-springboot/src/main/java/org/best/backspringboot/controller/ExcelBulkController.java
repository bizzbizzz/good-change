package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.excel.ExcelUploadResultDto;
import org.best.backspringboot.exception.BulkUploadException;
import org.best.backspringboot.service.ExcelBulkService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 엑셀 일괄 등록 (SUPER_ADMIN 전용)
 *  - 수혜자(member) 일괄 등록
 *  - 가맹점(merchant) 일괄 등록
 *  - 템플릿 다운로드
 */
@Tag(name = "엑셀파일", description = "엑셀파일 관련 API")
@RestController
@RequestMapping("/api/admin/excel")
@RequiredArgsConstructor
public class ExcelBulkController {

    private final ExcelBulkService excelBulkService;


    @Operation(summary = "수혜자 일괄 등록",
            description = "엑셀 파일로 수혜자를 일괄 등록. 하나라도 실패 시 전체 롤백")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 오류 (에러 목록 반환, 전체 롤백)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음 (SUPER_ADMIN만 가능)", content = @Content)
    })
    @PostMapping("/members")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ExcelUploadResultDto> uploadMembers(
            @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(excelBulkService.bulkInsertMembers(file));
        } catch (BulkUploadException e) {
            return ResponseEntity.badRequest().body(e.getResult());  // 에러 목록 반환
        }
    }


    @Operation(summary = "가맹점 일괄 등록",
            description = "엑셀 파일로 가맹점을 일괄 등록. 하나라도 실패 시 전체 롤백")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 오류 (에러 목록 반환, 전체 롤백)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음 (SUPER_ADMIN만 가능)", content = @Content)
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/merchants")
    public ResponseEntity<ExcelUploadResultDto> uploadMerchants(
            @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(excelBulkService.bulkInsertMerchants(file));
        } catch (BulkUploadException e) {
            return ResponseEntity.badRequest().body(e.getResult());  // 에러 목록 반환
        }
    }


    @Operation(summary = "수혜자 일괄등록 템플릿 다운로드",
            description = "수혜자 엑셀 등록용 템플릿 파일 다운로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음 (SUPER_ADMIN만 가능)", content = @Content)
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/members/template")
    public ResponseEntity<Resource> downloadMemberTemplate() throws Exception {
        byte[] bytes = excelBulkService.createMemberTemplate();
        return buildDownload(bytes, "수혜자_일괄등록_템플릿.xlsx");
    }


    @Operation(summary = "가맹점 일괄등록 템플릿 다운로드",
            description = "가맹점 엑셀 등록용 템플릿 파일 다운로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음 (SUPER_ADMIN만 가능)", content = @Content)
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/merchants/template")
    public ResponseEntity<Resource> downloadMerchantTemplate() throws Exception {
        byte[] bytes = excelBulkService.createMerchantTemplate();
        return buildDownload(bytes, "가맹점_일괄등록_템플릿.xlsx");
    }


    // ── 다운로드 응답 빌더 ───────────────────────────────────
    private ResponseEntity<Resource> buildDownload(byte[] bytes, String filename) throws Exception {
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encoded)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(bytes));
    }
}
