package org.best.backspringboot.card.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardListMapper {

    int bulkInsert(@Param("list") List<String> cardNumbers);
    boolean existsByCardNumber(@Param("cardNumber") String cardNumber);
    void deleteByCardNumber(@Param("cardNumber") String cardNumber);   // 추가
    void insertOne(@Param("cardNumber") String cardNumber);            // 추가
}