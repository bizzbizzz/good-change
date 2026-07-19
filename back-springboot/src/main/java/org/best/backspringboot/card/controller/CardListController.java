package org.best.backspringboot.card.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.card.dto.card.CardListBulkDto;
import org.best.backspringboot.card.service.CardListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "카드 목록", description = "카드번호 풀(card_list) 관련 API")
@RestController
@RequestMapping("/api/card-list")
@RequiredArgsConstructor
public class CardListController {

    private final CardListService cardListService;

    @Operation(summary = "카드번호 일괄 등록",
               description = "발급한 카드번호를 card_list 풀에 일괄 적재. 중복/형식오류는 건너뜀")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공 (성공/실패 건수 반환)"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> bulkInsert(@RequestBody CardListBulkDto dto) {
        return ResponseEntity.ok(cardListService.bulkInsert(dto.getCardNumbers()));
    }

    @Operation(summary = "카드번호 랜덤 생성 및 등록",
               description = "지정한 개수만큼 랜덤 카드번호를 생성해 card_list에 등록. SUPER_ADMIN 전용")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 및 등록 성공"),
            @ApiResponse(responseCode = "400", description = "개수 범위 오류 (1~1000)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음 (SUPER_ADMIN만 가능)", content = @Content)
    })
    @PostMapping("/generate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> generate(@RequestParam int count) {
        return ResponseEntity.ok(cardListService.generateAndInsert(count));
    }

    @Operation(summary = "카드번호 유효성 검증 (발급된 카드인지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검증 완료 (valid: true/false)"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> verifyCardNumber(@RequestParam String cardNumber) {
        boolean valid = cardListService.isInCardList(cardNumber);
        return ResponseEntity.ok(Map.of("valid", valid));
    }
}