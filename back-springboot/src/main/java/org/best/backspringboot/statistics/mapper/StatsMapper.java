package org.best.backspringboot.statistics.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.statistics.dto.statistics.DailyPaymentDto;
import org.best.backspringboot.statistics.dto.statistics.MonthlySettlementDto;

import java.util.List;

@Mapper
public interface StatsMapper {
    // 일별 총 결제금액 (최근 30일)
    List<DailyPaymentDto> findDailyPayment(@Param("days") int days);

    // 월별 총 정산금액 (최근 12개월)
    List<MonthlySettlementDto> findMonthlySettlement(@Param("months") int months);
}
