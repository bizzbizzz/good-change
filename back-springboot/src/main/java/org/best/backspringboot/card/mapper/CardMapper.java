package org.best.backspringboot.card.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.card.dto.card.CardCreateDto;
import org.best.backspringboot.card.dto.card.CardSearchDto;
import org.best.backspringboot.card.dto.card.CardUpdateDto;
import org.best.backspringboot.card.entity.Card;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CardMapper {
    List<Card> findAll(CardSearchDto dto);
    long countAll(CardSearchDto dto);
    List<Card> findByMemberId(Long memberId);
    Optional<Card> findByCardNumber(String cardNumber);
    Optional<Card> findById(Long cardId);
    void insert(CardCreateDto dto);
    long countByMemberId(Long memberId);
    void delete(Long cardId);
    void update(Long cardId, CardUpdateDto dto);
    Optional<String> findPrimaryCardNumberByMemberId(Long memberId);
    void disableByMemberId(Long memberId);  // ✅ 추가
    List<Card> findAllByMemberId(Long memberId);        // ✅ 추가 (전체)
}