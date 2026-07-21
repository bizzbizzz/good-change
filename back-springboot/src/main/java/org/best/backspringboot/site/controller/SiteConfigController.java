package org.best.backspringboot.site.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.site.entity.SiteConfig;
import org.best.backspringboot.site.service.SiteConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@Tag(name = "사이트 설정", description = "사이트 설정 API")
@RestController
@RequestMapping("/api/site-config")
@RequiredArgsConstructor
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    @Operation(summary = "설정 전체 조회")
    @GetMapping
    public ResponseEntity<List<SiteConfig>> getAll(
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) String configVal) {
        return ResponseEntity.ok(siteConfigService.getAll(configKey, configVal));
    }

    @Operation(summary = "설정 등록")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody SiteConfig dto) {
        siteConfigService.insert(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "설정 수정")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping("/{configKey}")
    public ResponseEntity<Void> update(@PathVariable String configKey,
                                       @RequestBody Map<String, Object> body) {
        siteConfigService.update(
                configKey,
                (String) body.get("configVal"),
                body.get("sortNo") != null ? Integer.valueOf(body.get("sortNo").toString()) : null,
                (String) body.get("useYn")
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "설정 삭제")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{configKey}")
    public ResponseEntity<Void> delete(@PathVariable String configKey) {
        siteConfigService.delete(configKey);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "설정 단건 조회")
    @GetMapping("/{configKey}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<SiteConfig> getByKey(@PathVariable String configKey) {
        return siteConfigService.findByKey(configKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}