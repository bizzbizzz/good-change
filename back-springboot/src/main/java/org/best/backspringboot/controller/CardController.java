package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.card.CardResponseDto;
import org.best.backspringboot.dto.card.CardSearchDto;
import org.best.backspringboot.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "카드", description = "카드 관련 API")
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @Operation(summary = "카드번호로 카드 조회")
    @GetMapping("/{cardNumber}")
    public ResponseEntity<CardResponseDto> getByCardNumber(@PathVariable String cardNumber) {
        return ResponseEntity.ok(cardService.getByCardNumber(cardNumber));
    }

    @Operation(summary = "카드 전체 조회 (페이징)")
    @GetMapping
    public ResponseEntity<PageResponse<CardResponseDto>> getAll(CardSearchDto dto) {
        return ResponseEntity.ok(cardService.getAll(dto));
    }

    @Operation(summary = "회원별 카드 조회")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<CardResponseDto>> getByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(cardService.getByMemberId(memberId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "카드 등록")
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CardCreateDto dto) {
        cardService.create(dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "카드 삭제")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> delete(@PathVariable Long cardId) {
        cardService.delete(cardId);
        return ResponseEntity.ok().build();
    }
}