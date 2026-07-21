package org.best.backspringboot.member.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.global.commonDTO.PageResponse;
import org.best.backspringboot.member.dto.memberInquiry.MemberInquiryRequestDto;
import org.best.backspringboot.member.dto.memberInquiry.MemberInquiryResponseDto;
import org.best.backspringboot.member.dto.memberInquiry.MemberInquirySearchDto;
import org.best.backspringboot.member.entity.MemberInquiry;
import org.best.backspringboot.member.mapper.MemberInquiryMapper;
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

    @Transactional
    public void updateStatusByIds(List<Long> ids) {
        memberInquiryMapper.updateStatusByIds(ids);
    }

    @Transactional
    public void updateStatusToWaitByIds(List<Long> ids) {
        memberInquiryMapper.updateStatusToWaitByIds(ids);
    }

    @Transactional
    public void deleteByIds(List<Long> ids) {
        memberInquiryMapper.deleteByIds(ids);
    }
}