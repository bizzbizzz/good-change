package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.entity.Card;
import java.util.Optional;

@Mapper
public interface CardMapper {
    Optional<Card> findByCardNumber(String cardNumber);
    Optional<Card> findById(Long cardId);
}