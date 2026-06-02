package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryRequestDto;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryResponseDto;
import org.best.backspringboot.entity.MemberInquiry;
import org.best.backspringboot.mapper.MemberInquiryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberInquiryService {

    private final MemberInquiryMapper memberInquiryMapper;

    // 문의 등록
    @Transactional
    public void insertInquiry(MemberInquiryRequestDto dto) {
        memberInquiryMapper.insertInquiry(dto);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<MemberInquiryResponseDto> getAllInquiry() {
        return memberInquiryMapper.selectAllInquiry().stream()
                .map(MemberInquiryResponseDto::from)
                .collect(Collectors.toList());
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public MemberInquiryResponseDto getInquiryById(Long id) {
        MemberInquiry entity = memberInquiryMapper.selectInquiryById(id);
        if (entity == null) {
            throw new IllegalArgumentException("존재하지 않는 문의입니다.");
        }
        return MemberInquiryResponseDto.from(entity);
    }

}