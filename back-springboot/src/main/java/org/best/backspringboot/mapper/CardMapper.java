package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.card.CardSearchDto;
import org.best.backspringboot.entity.Card;

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
}