package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.card.CardResponseDto;
import org.best.backspringboot.dto.card.CardSearchDto;
import org.best.backspringboot.dto.card.CardUpdateDto;
import org.best.backspringboot.entity.Card;
import org.best.backspringboot.entity.Member;
import org.best.backspringboot.mapper.CardListMapper;
import org.best.backspringboot.mapper.CardMapper;
import org.best.backspringboot.mapper.CardReissueHistoryMapper;
import org.best.backspringboot.mapper.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardMapper cardMapper;
    private final MemberMapper memberMapper;
    private final CardListMapper cardListMapper;
    private final CardReissueHistoryService cardReissueHistoryService;

    @Transactional(readOnly = true)
    public CardResponseDto getByCardNumber(String cardNumber) {
        Card card = cardMapper.findByCardNumber(cardNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드번호입니다."));

        Member member = memberMapper.findById(card.getMemberId())
                .orElse(null);

        return CardResponseDto.from(card, member);
    }

    @Transactional(readOnly = true)
    public PageResponse<CardResponseDto> getAll(CardSearchDto dto) {
        PageResponse<CardResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(dto.getPage());
        pageResponse.setSize(dto.getSize());

        List<CardResponseDto> content = cardMapper.findAll(dto).stream()
                .map(card -> {
                    Member member = memberMapper.findById(card.getMemberId()).orElse(null);
                    return CardResponseDto.from(card, member);
                })
                .collect(Collectors.toList());

        long totalCount = cardMapper.countAll(dto);
        pageResponse.setPageInfo(content, totalCount);
        return pageResponse;
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getByMemberId(Long memberId) {
        return cardMapper.findByMemberId(memberId).stream()
                .map(card -> {
                    Member member = memberMapper.findById(card.getMemberId()).orElse(null);
                    return CardResponseDto.from(card, member);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void create(CardCreateDto dto) {
        // 카드번호 중복 체크
        cardMapper.findByCardNumber(dto.getCardNumber())
                .ifPresent(c -> { throw new IllegalArgumentException("이미 등록된 카드번호입니다."); });

        // 카드 최대 3장 체크 (고유카드 1장 + 추가카드 2장)
        long count = cardMapper.countByMemberId(dto.getMemberId());
        if (count >= 3) {
            throw new IllegalArgumentException("카드는 최대 3장까지 등록 가능합니다.");
        }

        // isPrimary 기본값 설정 (첫번째 카드면 고유카드)
        if (dto.getIsPrimary() == null) {
            dto.setIsPrimary(count == 0 ? 1 : 0);
        }

        cardMapper.insert(dto);
    }

    @Transactional
    public void update(Long cardId, CardUpdateDto dto) {
        cardMapper.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드입니다."));
        cardMapper.update(cardId, dto);
    }

    @Transactional
    public void delete(Long cardId) {
        cardMapper.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드입니다."));
        cardMapper.delete(cardId);
    }

    @Transactional(readOnly = true)
    public CardResponseDto getCardInfo(String cardNumber, String memberName, String birthDate) {
        Card card = cardMapper.findByCardNumber(cardNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드입니다."));

        Member member = memberMapper.findById(card.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!member.getName().equals(memberName)) {
            throw new IllegalArgumentException("카드 정보가 일치하지 않습니다.");
        }

        if (birthDate != null && !birthDate.isEmpty()
                && !member.getBirthDate().equals(birthDate)) {
            throw new IllegalArgumentException("카드 정보가 일치하지 않습니다.");
        }

        return CardResponseDto.from(card, member);
    }

    @Transactional
    public Map<String, Object> reissue(Long cardId, String reason) {
        // 1. 기존 카드 조회
        Card oldCard = cardMapper.findById(cardId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카드입니다."));

        if ("DELETED".equals(oldCard.getStatus())) {
            throw new RuntimeException("삭제된 카드는 재발급할 수 없습니다.");
        }

        // 2. 기존 카드 → BLOCKED + is_primary = 0
        CardUpdateDto blockDto = new CardUpdateDto();
        blockDto.setStatus("BLOCKED");
        blockDto.setIsPrimary(0);
        cardMapper.update(cardId, blockDto);

        // 3. card_list에서 기존 카드번호 제거
        cardListMapper.deleteByCardNumber(oldCard.getCardNumber());

        // 4. 새 카드번호 랜덤 생성
        String newCardNumber = generateUniqueCardNumber();

        // 5. card_list에 새 번호 등록
        cardListMapper.insertOne(newCardNumber);

        // 6. card 테이블에 새 카드 등록
        CardCreateDto newCard = new CardCreateDto();
        newCard.setMemberId(oldCard.getMemberId());
        newCard.setCardNumber(newCardNumber);
        newCard.setCardAlias(oldCard.getCardAlias());
        newCard.setIsPrimary(1);
        cardMapper.insert(newCard);  // insert 후 newCard.getCardId()에 자동 채워짐

        // 7. 재발급 이력 저장
        cardReissueHistoryService.save(
                oldCard.getCardId(), oldCard.getCardNumber(),
                newCard.getCardId(), newCardNumber,
                oldCard.getMemberId(), reason
        );

        Map<String, Object> result = new HashMap<>();
        result.put("oldCardNumber", oldCard.getCardNumber());
        result.put("newCardNumber", newCardNumber);
        result.put("memberId", oldCard.getMemberId());
        result.put("reason", reason);
        return result;
    }

    private String generateUniqueCardNumber() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {  // 최대 100번 시도
            long rest = (long)(random.nextDouble() * 1_000_000_000_000L);
            String cardNumber = "9876" + String.format("%012d", rest);

            // card_list에 없는 번호인지 확인
            if (!cardListMapper.existsByCardNumber(cardNumber)) {
                return cardNumber;
            }
        }
        throw new RuntimeException("카드번호 생성에 실패했습니다. 관리자에게 문의하세요.");
    }
}