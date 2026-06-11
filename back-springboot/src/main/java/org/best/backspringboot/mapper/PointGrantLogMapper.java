package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.dto.pointgrant.PointGrantLogSearchDto;
import org.best.backspringboot.dto.pointgrant.PointGrantLogSummaryDto;
import org.best.backspringboot.entity.PointGrantLog;

import java.util.List;

@Mapper
public interface PointGrantLogMapper {
    void insert(PointGrantLog log);
    List<PointGrantLog> findAll(PointGrantLogSearchDto searchDto);
    long countAll(PointGrantLogSearchDto searchDto);
    List<PointGrantLog> findByGrantId(@Param("grantId") String grantId);
    List<PointGrantLogSummaryDto> findSummaryAll(PointGrantLogSearchDto searchDto);
    long countSummaryAll(PointGrantLogSearchDto searchDto);
}
