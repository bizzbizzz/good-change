package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryRequestDto;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryResponseDto;
import org.best.backspringboot.dto.memberInquiry.MemberInquirySearchDto;
import org.best.backspringboot.entity.MemberInquiry;

import java.util.List;

@Mapper
public interface MemberInquiryMapper {

    void insertInquiry(MemberInquiryRequestDto dto);

    List<MemberInquiry> selectAllInquiry(MemberInquirySearchDto searchDto);

    long countAll(MemberInquirySearchDto searchDto);

    MemberInquiry selectInquiryById(Long id);

    void updateStatus(Long id);

    void updateStatusByIds(List<Long> ids);
    void updateStatusToWaitByIds(List<Long> ids);
    void deleteByIds(List<Long> ids);
}