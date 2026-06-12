package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.entity.MerchantCategory;
import org.best.backspringboot.service.MerchantCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "가맹점 카테고리", description = "가맹점 카테고리 관리 API (SUPER_ADMIN 전용)")
@RestController
@RequestMapping("/api/merchant-categories")
@RequiredArgsConstructor
public class MerchantCategoryController {

    private final MerchantCategoryService merchantCategoryService;

    @Operation(summary = "카테고리 전체 조회")
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<MerchantCategory>> getAll() {
        return ResponseEntity.ok(merchantCategoryService.getAll());
    }

    @Operation(summary = "카테고리 등록")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> create(@RequestBody Map<String, String> body) {
        merchantCategoryService.create(body.get("categoryName"), body.get("description"));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카테고리 수정")
    @PatchMapping("/{categoryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long categoryId,
                                       @RequestBody Map<String, String> body) {
        merchantCategoryService.update(categoryId, body.get("categoryName"), body.get("description"));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카테고리 삭제")
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId) {
        merchantCategoryService.delete(categoryId);
        return ResponseEntity.ok().build();
    }
}
