package org.best.backspringboot.card.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.card.entity.Bank;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BankMapper {
    List<Bank> findAll();
    Optional<Bank> findById(Long bankId);
    void insert(@Param("bankName") String bankName, @Param("bankCode") String bankCode);
    void update(@Param("bankId") Long bankId,
                @Param("bankName") String bankName,
                @Param("bankCode") String bankCode,
                @Param("status") String status);
    void delete(Long bankId); // INACTIVE 처리
}