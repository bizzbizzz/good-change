package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.SearchBase;
import org.best.backspringboot.dto.member.MemberRegisterDto;
import org.best.backspringboot.dto.merchant.*;
import org.best.backspringboot.mapper.MemberMapper;
import org.best.backspringboot.mapper.MerchantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantMapper merchantMapper;
    private final MemberService memberService;  // 추가

    @Transactional
    public void create(MerchantCreateDto dto) {
        merchantMapper.findByBusinessNumber(dto.getBusinessNumber())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 등록된 사업자번호입니다."); });
        merchantMapper.insert(dto);
    }

    @Transactional(readOnly = true)
    public MerchantResponseDto getByMemberId(Long memberId) {
        return merchantMapper.findByMemberId(memberId)
                .map(m -> {
                    String categoryName = m.getCategoryId() != null
                            ? merchantMapper.findCategoryNameById(m.getCategoryId()) : null;
                    return MerchantResponseDto.from(m,
                            categoryName != null ? List.of(categoryName) : List.of());
                })
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 가맹점이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public MerchantResponseDto getById(Long merchantId) {
        return merchantMapper.findById(merchantId)
                .map(m -> {
                    String categoryName = m.getCategoryId() != null
                            ? merchantMapper.findCategoryNameById(m.getCategoryId()) : null;
                    return MerchantResponseDto.from(m,
                            categoryName != null ? List.of(categoryName) : List.of());
                })
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));
    }

    @Transactional
    public void createWithMember(MerchantRegisterDto dto) {
        // member insert
        memberService.create(dto.getMember());
        Long memberId = dto.getMember().getMember().getMemberId();

        // merchant insert
        dto.getMerchant().setMemberId(memberId);
        merchantMapper.findByBusinessNumber(dto.getMerchant().getBusinessNumber())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 등록된 사업자번호입니다."); });
        merchantMapper.insert(dto.getMerchant());
    }

    @Transactional(readOnly = true)
    public PageResponse<MerchantResponseDto> getAll(MerchantSearchDto searchBase) {
        PageResponse<MerchantResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(searchBase.getPage());
        pageResponse.setSize(searchBase.getSize());

        List<MerchantResponseDto> content = merchantMapper.findAll(searchBase).stream()
                .map(m -> {
                    String categoryName = m.getCategoryId() != null
                            ? merchantMapper.findCategoryNameById(m.getCategoryId()) : null;
                    return MerchantResponseDto.from(m,
                            categoryName != null ? List.of(categoryName) : List.of());
                })
                .collect(Collectors.toList());

        long totalCount = merchantMapper.countAll();
        pageResponse.setPageInfo(content, totalCount);
        return pageResponse;
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return merchantMapper.findAllCategories();
    }

    @Transactional
    public void update(Long merchantId, MerchantUpdateDto dto) {
        merchantMapper.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));
        merchantMapper.update(merchantId, dto);
    }

    @Transactional
    public void delete(Long merchantId) {
        merchantMapper.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));
        merchantMapper.delete(merchantId);
    }
}