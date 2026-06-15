package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.card.CardResponseDto;
import org.best.backspringboot.dto.card.CardSearchDto;
import org.best.backspringboot.dto.card.CardUpdateDto;
import org.best.backspringboot.dto.cardHistory.CardReissueHistorySearchDto;
import org.best.backspringboot.entity.CardReissueHistory;
import org.best.backspringboot.service.CardReissueHistoryService;
import org.best.backspringboot.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "카드", description = "카드 관련 API")
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final CardReissueHistoryService cardReissueHistoryService;

    @Operation(summary = "카드번호로 카드 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "카드 없음", content = @Content)
    })
    @GetMapping("/{cardNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CardResponseDto> getByCardNumber(@PathVariable String cardNumber) {
        return ResponseEntity.ok(cardService.getByCardNumber(cardNumber));
    }

    @Operation(summary = "카드 전체 조회 (페이징)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PageResponse<CardResponseDto>> getAll(CardSearchDto dto) {
        return ResponseEntity.ok(cardService.getAll(dto));
    }

    @Operation(summary = "회원별 카드 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
    })
    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    public ResponseEntity<List<CardResponseDto>> getByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(cardService.getByMemberId(memberId));
    }

    @Operation(summary = "카드 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (중복/최대 초과 등)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    public ResponseEntity<Void> create(@Valid @RequestBody CardCreateDto dto) {
        cardService.create(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "신원 정보 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "카드 정보 불일치", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "카드 없음", content = @Content)
    })
    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    public ResponseEntity<CardResponseDto> getCardInfo(
            @RequestParam String cardNumber,
            @RequestParam String memberName,
            @RequestParam(required = false) String birthDate) {
        return ResponseEntity.ok(cardService.getCardInfo(cardNumber, memberName, birthDate));
    }

    @Operation(summary = "카드 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "카드 없음", content = @Content)
    })
    @PatchMapping("/{cardId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    public ResponseEntity<Void> update(@PathVariable Long cardId,
                                       @RequestBody CardUpdateDto dto) {
        cardService.update(cardId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카드 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "카드 없음", content = @Content)
    })
    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MERCHANT')")
    public ResponseEntity<Void> delete(@PathVariable Long cardId) {
        cardService.delete(cardId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "카드 재발급",
            description = "기존 카드 정지 + card_list 제거 + 새 카드 랜덤 생성·발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "카드 없음", content = @Content)
    })
    @PostMapping("/{cardId}/reissue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> reissue(
            @PathVariable Long cardId,
            @RequestBody Map<String, String> body) {

        String reason = body.getOrDefault("reason", "OTHER");
        Map<String, Object> result = cardService.reissue(cardId, reason);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "재발급 이력 목록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 안 됨", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/reissue-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PageResponse<CardReissueHistory>> getReissueHistory(
            CardReissueHistorySearchDto dto) {

        return ResponseEntity.ok(cardReissueHistoryService.getAll(dto));
    }
}