package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.SearchBase;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.member.*;
import org.best.backspringboot.entity.Member;
import org.best.backspringboot.mapper.CardMapper;
import org.best.backspringboot.mapper.MemberMapper;
import org.best.backspringboot.mapper.MerchantMapper;
import org.best.backspringboot.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final MerchantMapper merchantMapper; // ✅ 추가
    private final CardMapper cardMapper;
    private final JwtUtil jwtUtil;

    @Transactional
    public void create(MemberRegisterDto registerDto) {
        // 아이디 중복 체크
        memberMapper.findByLoginId(registerDto.getMember().getLoginId())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 사용 중인 아이디입니다."); });
        // 비밀번호 암호화
        registerDto.getMember().encodePassword(passwordEncoder);
        memberMapper.insert(registerDto.getMember());

        // 첫 번째 카드는 고유카드(isPrimary=1), 나머지는 추가카드(isPrimary=0)
        for (int i = 0; i < registerDto.getCards().size(); i++) {
            CardCreateDto cardDto = CardCreateDto.builder()
                    .memberId(registerDto.getMember().getMemberId())
                    .cardNumber(registerDto.getCards().get(i).getCardNumber())
                    .cardAlias(registerDto.getCards().get(i).getCardAlias())  // 추가
                    .isPrimary(i == 0 ? 1 : 0)
                    .build();
            cardMapper.insert(cardDto);
        }
    }

    @Transactional(readOnly = true)
    public MemberResponseDto getById(Long memberId) {
        return memberMapper.findById(memberId)
                .map(MemberResponseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional(readOnly = true)
    public PageResponse<MemberResponseDto> getAll(MemberSearchDto searchDto) {
        PageResponse<MemberResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(searchDto.getPage());
        pageResponse.setSize(searchDto.getSize());

        List<MemberResponseDto> content = memberMapper.findAll(searchDto).stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());

        long totalCount = memberMapper.countAll(searchDto);
        pageResponse.setPageInfo(content, totalCount);
        return pageResponse;
    }

    @Transactional
    public void update(String loginId, MemberUpdateDto dto) {
        memberMapper.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        memberMapper.update(loginId, dto);
    }

    @Transactional
    public void delete(String loginId) {
        memberMapper.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        memberMapper.delete(loginId);
    }

    // 아이디 중복체크 (true = 사용가능, false = 중복)
    public boolean isLoginIdAvailable(String loginId) {
        return memberMapper.findByLoginId(loginId).isEmpty();
    }

    // 로그인
    public String login(MemberLoginDto dto) {
        Member member = memberMapper.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // ✅ 추가 - 상태 체크
        if (!member.getStatus().equals("ACTIVE")) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        // ✅ 수정 - role, merchantId 추가
        String roleName = memberMapper.findRoleNameById(member.getRoleId());
        Long merchantId = null;
        if ("MERCHANT".equals(roleName)) {
            merchantId = merchantMapper.findMerchantIdByMemberId(member.getMemberId());
        }

        return jwtUtil.generateToken(member.getMemberId(), member.getLoginId(), roleName, merchantId);
    }
}