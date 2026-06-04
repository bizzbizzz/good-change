package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.SearchBase;
import org.best.backspringboot.dto.allowedip.AllowedIpCreateDto;
import org.best.backspringboot.dto.member.MemberCreateDto;
import org.best.backspringboot.dto.member.MemberRegisterDto;
import org.best.backspringboot.dto.member.MemberUpdateDto;
import org.best.backspringboot.dto.merchant.*;
import org.best.backspringboot.entity.Member;
import org.best.backspringboot.entity.Merchant;
import org.best.backspringboot.mapper.AllowedIpMapper;
import org.best.backspringboot.mapper.MemberMapper;
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
    private final MemberMapper memberMapper;
    private final AllowedIpMapper allowedIpMapper;

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
        MemberCreateDto member = dto.getMember().getMember();
        memberMapper.findByLoginId(member.getLoginId())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 사용 중인 아이디입니다: " + member.getLoginId()); });
        member.encodePassword(passwordEncoder);
        memberMapper.insert(member);

        // merchant insert
        merchantMapper.findByBusinessNumber(dto.getMerchant().getBusinessNumber())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 등록된 사업자번호입니다."); });
        dto.getMerchant().setMemberId(member.getMemberId());
        merchantMapper.insert(dto.getMerchant());

        // IP 등록
        if (!allowedIpMapper.existsByIp(dto.getIpAddress())) {
            allowedIpMapper.insert(AllowedIpCreateDto.builder()
                    .ipAddress(dto.getIpAddress())
                    .merchantId(dto.getMerchant().getMerchantId())
                    .description(dto.getMerchant().getMerchantName() + " 등록 IP")
                    .build());
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<MerchantResponseDto> getAll(MerchantSearchDto searchBase) {
        PageResponse<MerchantResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(searchBase.getPage());
        pageResponse.setSize(searchBase.getSize());

        List<MerchantResponseDto> content = merchantMapper.findAll(searchBase).stream()
                .map(m -> MerchantResponseDto.from(m,
                        m.getCategoryName() != null ? List.of(m.getCategoryName()) : List.of()))
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
        Merchant merchant = merchantMapper.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));

        // merchant 테이블 업데이트
        merchantMapper.update(merchantId, dto);

        // member 테이블 업데이트 (loginId, password)
        if (merchant.getMemberId() != null) {
            MemberUpdateDto memberUpdateDto = new MemberUpdateDto();
            boolean hasUpdate = false;

            if (dto.getLoginId() != null && !dto.getLoginId().isEmpty()) {
                memberUpdateDto.setLoginId(dto.getLoginId());
                hasUpdate = true;
            }
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                memberUpdateDto.setPassword(passwordEncoder.encode(dto.getPassword()));
                hasUpdate = true;
            }
            if (dto.getDetailAddress() != null) {
                memberUpdateDto.setDetailAddress(dto.getDetailAddress());
                hasUpdate = true;
            }

            // 업데이트할 필드가 있을 때만 실행
            if (hasUpdate) {
                memberMapper.update(merchant.getMemberId(), memberUpdateDto);
            }
        }
    }

    @Transactional
    public void delete(Long merchantId) {
        merchantMapper.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));
        merchantMapper.delete(merchantId);
    }

    @Transactional(readOnly = true)
    public String getMerchantNameByMemberId(Long memberId) {
        try {
            return merchantMapper.findByMemberId(memberId)
                    .map(m -> m.getMerchantName())
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

}