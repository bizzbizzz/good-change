package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.dto.settlement.SettlementSearchDto;
import org.best.backspringboot.entity.Settlement;
import java.util.List;
import java.util.Optional;

@Mapper
public interface SettlementMapper {
    List<Settlement> findAll(SettlementSearchDto dto);
    long countAll(SettlementSearchDto dto);
    Optional<Settlement> findById(Long settlementId);
    void updateStatusByMonth(@Param("merchantId") Long merchantId,
                             @Param("settlementMonth") String settlementMonth,
                             @Param("status") String status);
    long sumAmount(SettlementSearchDto dto);
}