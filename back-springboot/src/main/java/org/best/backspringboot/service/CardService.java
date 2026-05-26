package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.card.CardResponseDto;
import org.best.backspringboot.dto.card.CardSearchDto;
import org.best.backspringboot.dto.card.CardUpdateDto;
import org.best.backspringboot.entity.Card;
import org.best.backspringboot.entity.Member;
import org.best.backspringboot.mapper.CardMapper;
import org.best.backspringboot.mapper.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardMapper cardMapper;
    private final MemberMapper memberMapper;

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
}