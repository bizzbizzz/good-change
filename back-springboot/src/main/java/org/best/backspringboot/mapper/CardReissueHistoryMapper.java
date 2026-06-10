package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.dto.cardHistory.CardReissueHistorySearchDto;
import org.best.backspringboot.entity.CardReissueHistory;

import java.util.List;

@Mapper
public interface CardReissueHistoryMapper {

    void insert(@Param("oldCardId") Long oldCardId,
                @Param("oldCardNumber") String oldCardNumber,
                @Param("newCardId") Long newCardId,
                @Param("newCardNumber") String newCardNumber,
                @Param("memberId") Long memberId,
                @Param("reason") String reason);

    // 회원별 재발급 이력 조회
    List<CardReissueHistory> findByMemberId(@Param("memberId") Long memberId);

    // 카드별 재발급 이력 조회
    List<CardReissueHistory> findByOldCardId(@Param("oldCardId") Long oldCardId);
    List<CardReissueHistory> findAll(@Param("dto") CardReissueHistorySearchDto dto);
    long countAll(@Param("dto") CardReissueHistorySearchDto dto);
}