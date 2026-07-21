package org.best.backspringboot.card.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.global.commonDTO.PageResponse;
import org.best.backspringboot.card.dto.card.cardHistory.CardReissueHistorySearchDto;
import org.best.backspringboot.card.entity.CardReissueHistory;
import org.best.backspringboot.card.mapper.CardReissueHistoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardReissueHistoryService {

    private final CardReissueHistoryMapper cardReissueHistoryMapper;

    @Transactional(readOnly = true)
    public PageResponse<CardReissueHistory> getAll(CardReissueHistorySearchDto dto) {
        List<CardReissueHistory> content = cardReissueHistoryMapper.findAll(dto);
        long total = cardReissueHistoryMapper.countAll(dto);

        PageResponse<CardReissueHistory> response = new PageResponse<>();
        response.setPage(dto.getPage());
        response.setSize(dto.getSize());
        response.setPageInfo(content, total);
        return response;
    }

    @Transactional(readOnly = true)
    public List<CardReissueHistory> getByMemberId(Long memberId) {
        return cardReissueHistoryMapper.findByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<CardReissueHistory> getByOldCardId(Long oldCardId) {
        return cardReissueHistoryMapper.findByOldCardId(oldCardId);
    }

    // CardReissueHistoryService에 추가
    @Transactional
    public void save(Long oldCardId, String oldCardNumber,
                     Long newCardId, String newCardNumber,
                     Long memberId, String reason) {
        cardReissueHistoryMapper.insert(oldCardId, oldCardNumber,
                newCardId, newCardNumber, memberId, reason);
    }
}