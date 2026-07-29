package org.best.backspringboot.merchant.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.card.mapper.CardMapper;
import org.best.backspringboot.global.commonDTO.PageResponse;
import org.best.backspringboot.member.entity.Member;
import org.best.backspringboot.member.entity.MemberWithdrawLog;
import org.best.backspringboot.member.entity.Role;
import org.best.backspringboot.member.mapper.MemberWithdrawLogMapper;
import org.best.backspringboot.member.mapper.RoleMapper;
import org.best.backspringboot.member.mapper.TokenMapper;
import org.best.backspringboot.merchant.dto.allowedip.AllowedIpCreateDto;
import org.best.backspringboot.member.dto.member.MemberCreateDto;
import org.best.backspringboot.member.dto.member.MemberUpdateDto;
import org.best.backspringboot.merchant.dto.merchant.*;
import org.best.backspringboot.merchant.dto.merchantMember.MerchantAddMemberDto;
import org.best.backspringboot.merchant.dto.merchantMember.MerchantMemberResponseDto;
import org.best.backspringboot.merchant.dto.merchantMember.MerchantMemberSearchDto;
import org.best.backspringboot.merchant.entity.Merchant;
import org.best.backspringboot.merchant.entity.MerchantMember;
import org.best.backspringboot.merchant.mapper.AllowedIpMapper;
import org.best.backspringboot.member.mapper.MemberMapper;
import org.best.backspringboot.merchant.mapper.MerchantMapper;
import org.best.backspringboot.merchant.mapper.MerchantMemberMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;
    private final AllowedIpMapper allowedIpMapper;
    private final MemberWithdrawLogMapper memberWithdrawLogMapper;
    private final MerchantMemberMapper merchantMemberMapper;
    private final TokenMapper tokenMapper;
    private final RoleMapper roleMapper;

    @Transactional
    public void statusChange(Long merchantId, String status) {
        Merchant merchant = merchantMapper.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));

        // 가맹점 비활성화
        merchantMapper.updateStatus(merchant.getMerchantId(), status);

        // ✅ 연결된 member도 비활성화
        List<MerchantMember> members = merchantMemberMapper.findByMerchantId(merchant.getMerchantId());
        members.forEach(mm -> memberMapper.updateStatusById(mm.getMemberId(), status));
    }

    @Transactional
    public void create(MerchantCreateDto dto) {
        merchantMapper.findByBusinessNumber(dto.getBusinessNumber())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 등록된 사업자번호입니다."); });
        merchantMapper.insert(dto);
    }

    @Transactional(readOnly = true)
    public MerchantResponseDto getByMemberId(Long memberId) {
        Long merchantId = merchantMemberMapper.findByMemberId(memberId)
                .map(MerchantMember::getMerchantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 가맹점이 존재하지 않습니다."));
        return getById(merchantId);
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
    public void addMember(Long merchantId, MerchantAddMemberDto dto) {

        // 1. 아이디 중복 체크 (이미 존재하면 안됨)
        memberMapper.findByLoginId(dto.getLoginId())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 사용 중인 아이디입니다."); });

        // 2. 회원 생성
        MemberCreateDto memberCreateDto = MemberCreateDto.builder()
                .loginId(dto.getLoginId())
                .name(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .birthDate(LocalDate.now())
                .gender("MALE")
                .roleId(dto.getRoleId())
                .build();
        memberMapper.insert(memberCreateDto);

        // 3. 가맹점 회원 추가
        merchantMemberMapper.insert(merchantId, memberCreateDto.getMemberId(), dto.getRoleId());
    }

    @Transactional
    public void createWithMember(MerchantRegisterDto dto) {
        // member insert
        MemberCreateDto member = dto.getMember().getMember();
        memberMapper.findByLoginId(member.getLoginId())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 사용 중인 아이디입니다: " + member.getLoginId()); });
        member.encodePassword(passwordEncoder);
        if (member.getPoint() == null) {
            member.setPoint(0L);
        }
        memberMapper.insert(member);

        // merchant insert
        merchantMapper.findByBusinessNumber(dto.getMerchant().getBusinessNumber())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 등록된 사업자번호입니다."); });
        merchantMapper.insert(dto.getMerchant());  // ✅ 추가

        // OWNER role_id 조회 후 INSERT
        Long ownerRoleId = roleMapper.findByRoleName("OWNER")
                .map(Role::getRoleId)
                .orElseThrow(() -> new IllegalArgumentException("OWNER 역할이 없습니다."));

        merchantMemberMapper.insert(
                dto.getMerchant().getMerchantId(),
                member.getMemberId(),
                ownerRoleId
        );

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

        long totalCount = merchantMapper.countAll(searchBase);  // ✅ searchBase 추가
        pageResponse.setPageInfo(content, totalCount);
        return pageResponse;
    }

    @Transactional
    public void withdraw(Long memberId, String password, String reason) {
        Member member = memberMapper.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        MerchantMember merchantMember = merchantMemberMapper.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점 회원입니다."));

        Merchant merchant = merchantMapper.findById(merchantMember.getMerchantId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));

        // 탈퇴 로그 저장
        MemberWithdrawLog log = MemberWithdrawLog.builder()
                .memberId(memberId)
                .loginId(member.getLoginId())
                .name(merchant.getMerchantName())
                .reason(reason)
                .build();
        memberWithdrawLogMapper.insert(log);

        if ("OWNER".equals(merchantMember.getRoleName())) {
            // ✅ OWNER → 가맹점 전체 탈퇴
            List<MerchantMember> allMembers = merchantMemberMapper.findByMerchantId(merchant.getMerchantId());

            // 모든 멤버 token 삭제
            allMembers.forEach(mm -> tokenMapper.deleteByMemberId(mm.getMemberId()));

            // merchant_member 삭제
            merchantMemberMapper.deleteByMerchantId(merchant.getMerchantId());

            // merchant 삭제
            merchantMapper.withdraw(merchant.getMerchantId());

            // 모든 멤버 삭제
            allMembers.forEach(mm -> memberMapper.withdraw(mm.getMemberId()));

        } else {
            // ✅ STAFF → 본인만 탈퇴
            tokenMapper.deleteByMemberId(memberId);
            merchantMemberMapper.deleteByMemberId(memberId);
            memberMapper.withdraw(memberId);
        }
    }



    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return merchantMapper.findAllCategories();
    }

    @Transactional(readOnly = true)
    public PageResponse<MerchantMemberResponseDto> getMerchantMembers(MerchantMemberSearchDto searchDto) {
        PageResponse<MerchantMemberResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(searchDto.getPage());
        pageResponse.setSize(searchDto.getSize());

        List<MerchantMemberResponseDto> content = merchantMemberMapper.findAll(searchDto).stream()
                .map(mm -> {
                    Merchant merchant = merchantMapper.findById(mm.getMerchantId()).orElse(null);
                    Member member     = memberMapper.findById(mm.getMemberId()).orElse(null);
                    Role role = roleMapper.findById(mm.getRoleId()).orElse(null);
                    if (merchant == null || member == null) return null;
                    return MerchantMemberResponseDto.from(mm, merchant, member, role);
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        long totalCount = merchantMemberMapper.countAll(searchDto);
        pageResponse.setPageInfo(content, totalCount);
        return pageResponse;
    }

    @Transactional
    public void update(Long merchantId, MerchantUpdateDto dto) {
        Merchant merchant = merchantMapper.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));

        MerchantMember mm = merchantMemberMapper.findByMerchantId(merchantId)
                .stream().findFirst()
                .orElse(null);

        // merchant 테이블 업데이트
        merchantMapper.update(merchantId, dto);

        // member 테이블 업데이트 (loginId, password)
        if (mm.getMemberId() != null) {
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
                memberMapper.update(mm.getMemberId(), memberUpdateDto);
            }
        }
    }

    @Transactional
    public void delete(Long merchantId) {
        merchantMapper.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));

        // ✅ merchantMember에서 memberId 조회
        List<MerchantMember> members = merchantMemberMapper.findByMerchantId(merchantId);

        // merchant_member 삭제
        merchantMemberMapper.deleteByMerchantId(merchantId);

        // merchant 삭제
        merchantMapper.delete(merchantId);

        // member 삭제
        members.forEach(mm -> memberMapper.delete(mm.getMemberId()));
    }

    @Transactional(readOnly = true)
    public String getMerchantNameByMemberId(Long memberId) {
        return merchantMemberMapper.findByMemberId(memberId)
                .map(mm -> merchantMapper.findById(mm.getMerchantId())
                        .map(Merchant::getMerchantName)
                        .orElse(null))
                .orElse(null);
    }

}