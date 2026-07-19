package org.best.backspringboot.statistics.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.statistics.dto.statistics.DailyPaymentDto;
import org.best.backspringboot.statistics.dto.statistics.MonthlySettlementDto;
import org.best.backspringboot.statistics.mapper.StatsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsMapper statsMapper;

    @Transactional(readOnly = true)
    public List<DailyPaymentDto> getDailyPayment(int days) {
        return statsMapper.findDailyPayment(days);
    }

    @Transactional(readOnly = true)
    public List<MonthlySettlementDto> getMonthlySettlement(int months) {
        return statsMapper.findMonthlySettlement(months);
    }
}
