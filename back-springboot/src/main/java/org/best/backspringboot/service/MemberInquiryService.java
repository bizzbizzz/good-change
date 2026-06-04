package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryRequestDto;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryResponseDto;
import org.best.backspringboot.dto.memberInquiry.MemberInquirySearchDto;
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

    @Transactional
    public void insertInquiry(MemberInquiryRequestDto dto) {
        memberInquiryMapper.insertInquiry(dto);
    }

    @Transactional(readOnly = true)
    public PageResponse<MemberInquiryResponseDto> getAllInquiry(MemberInquirySearchDto searchDto) {
        PageResponse<MemberInquiryResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(searchDto.getPage());
        pageResponse.setSize(searchDto.getSize());

        List<MemberInquiryResponseDto> content = memberInquiryMapper.selectAllInquiry(searchDto)
                .stream()
                .map(MemberInquiryResponseDto::from)
                .collect(Collectors.toList());

        long totalCount = memberInquiryMapper.countAll(searchDto);
        pageResponse.setPageInfo(content, totalCount);
        return pageResponse;
    }

    @Transactional(readOnly = true)
    public MemberInquiryResponseDto getInquiryById(Long id) {
        MemberInquiry entity = memberInquiryMapper.selectInquiryById(id);
        if (entity == null) {
            throw new IllegalArgumentException("존재하지 않는 문의입니다.");
        }
        return MemberInquiryResponseDto.from(entity);
    }

    @Transactional
    public void updateStatus(Long id) {
        MemberInquiry entity = memberInquiryMapper.selectInquiryById(id);
        if (entity == null) {
            throw new IllegalArgumentException("존재하지 않는 문의입니다.");
        }
        memberInquiryMapper.updateStatus(id);
    }
}