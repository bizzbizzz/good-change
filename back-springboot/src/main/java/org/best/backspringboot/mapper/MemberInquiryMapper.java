package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryRequestDto;
import org.best.backspringboot.entity.MemberInquiry;

import java.util.List;

@Mapper
public interface MemberInquiryMapper {

    void insertInquiry(MemberInquiryRequestDto dto);

    List<MemberInquiry> selectAllInquiry();

    MemberInquiry selectInquiryById(Long id);

}