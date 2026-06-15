package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.settlement.SettlementResponseDto;
import org.best.backspringboot.dto.settlement.SettlementSearchDto;
import org.best.backspringboot.mapper.SettlementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementMapper settlementMapper;

    @Transactional(readOnly = true)
    public PageResponse<SettlementResponseDto> getAll(SettlementSearchDto dto) {
        PageResponse<SettlementResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(dto.getPage());
        pageResponse.setSize(dto.getSize());

        List<SettlementResponseDto> content = settlementMapper.findAll(dto).stream()
                .map(SettlementResponseDto::from)
                .collect(Collectors.toList());

        long totalCount = settlementMapper.countAll(dto);
        pageResponse.setPageInfo(content, totalCount);

        // ✅ 정산금액 합계 추가
        long totalAmount = settlementMapper.sumAmount(dto);
        pageResponse.setTotalAmount(totalAmount);

        return pageResponse;
    }

    @Transactional(readOnly = true)
    public SettlementResponseDto getById(Long settlementId) {
        return settlementMapper.findById(settlementId)
                .map(SettlementResponseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정산내역입니다."));
    }

    @Transactional
    public void updateStatus(Long merchantId, String settlementMonth, String status) {
        settlementMapper.updateStatusByMonth(merchantId, settlementMonth, status);
    }
}