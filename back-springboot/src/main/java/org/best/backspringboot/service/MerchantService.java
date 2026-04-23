package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.SearchBase;
import org.best.backspringboot.dto.merchant.MerchantCreateDto;
import org.best.backspringboot.dto.merchant.MerchantResponseDto;
import org.best.backspringboot.dto.merchant.MerchantUpdateDto;
import org.best.backspringboot.mapper.MerchantMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void create(MerchantCreateDto dto) {
        // 아이디 중복 체크
        merchantMapper.findByLoginId(dto.getLoginId())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 사용 중인 아이디입니다."); });
        // 사업자번호 중복 체크
        merchantMapper.findByBusinessNumber(dto.getBusinessNumber())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 등록된 사업자번호입니다."); });

        merchantMapper.insert(dto);

        // 업종 등록
        if (dto.getCategories() != null) {
            for (String category : dto.getCategories()) {
                merchantMapper.insertCategory(dto.getMerchantId(), category);
            }
        }
    }

    @Transactional(readOnly = true)
    public MerchantResponseDto getById(Long merchantId) {
        return merchantMapper.findById(merchantId)
                .map(m -> MerchantResponseDto.from(m, merchantMapper.findCategoriesByMerchantId(merchantId)))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));
    }

    @Transactional(readOnly = true)
    public PageResponse<MerchantResponseDto> getAll(SearchBase searchBase) {
        PageResponse<MerchantResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(searchBase.getPage());
        pageResponse.setSize(searchBase.getSize());

        List<MerchantResponseDto> content = merchantMapper.findAll(searchBase).stream()
                .map(m -> MerchantResponseDto.from(m, merchantMapper.findCategoriesByMerchantId(m.getMerchantId())))
                .collect(Collectors.toList());

        long totalCount = merchantMapper.countAll();
        pageResponse.setPageInfo(content, totalCount);
        return pageResponse;
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