package org.best.backspringboot.controller;

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
@RestController
@RequestMapping("/api/admin/excel")
@RequiredArgsConstructor
public class ExcelBulkController {

    private final ExcelBulkService excelBulkService;


    // ── 수혜자 일괄 등록 ─────────────────────────────────────
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


    // ── 가맹점 일괄 등록 ─────────────────────────────────────
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


    // ── 수혜자 템플릿 다운로드 ───────────────────────────────
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/members/template")
    public ResponseEntity<Resource> downloadMemberTemplate() throws Exception {
        byte[] bytes = excelBulkService.createMemberTemplate();
        return buildDownload(bytes, "수혜자_일괄등록_템플릿.xlsx");
    }


    // ── 가맹점 템플릿 다운로드 ───────────────────────────────
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
