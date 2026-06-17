package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.entity.Bank;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BankMapper {
    List<Bank> findAll();               // 전체 은행 목록 (ACTIVE만)
    Optional<Bank> findById(Long bankId);
}
