package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardListMapper {

    int bulkInsert(@Param("list") List<String> cardNumbers);
    boolean existsByCardNumber(@Param("cardNumber") String cardNumber);
}