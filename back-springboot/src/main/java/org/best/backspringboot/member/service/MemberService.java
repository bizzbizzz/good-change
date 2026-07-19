package org.best.backspringboot.member.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.card.mapper.CardMapper;
import org.best.backspringboot.commonDTO.PageResponse;
import org.best.backspringboot.card.dto.card.CardCreateDto;
import org.best.backspringboot.member.dto.member.*;
import org.best.backspringboot.member.entity.Member;
import org.best.backspringboot.member.entity.PasswordResetToken;
import org.best.backspringboot.member.mapper.MemberMapper;
import org.best.backspringboot.member.mapper.PasswordResetTokenMapper;
import org.best.backspringboot.member.mapper.TokenMapper;
import org.best.backspringboot.merchant.mapper.MerchantMapper;
import org.best.backspringboot.mail.service.MailService;
import org.best.backspringboot.SSE.service.SseService;
import org.best.backspringboot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
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
    private final TokenMapper tokenMapper;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenMapper passwordResetTokenMapper;
    private final MailService mailService;
    private final SseService sseService;

    @Value("${app.reset-base-url}")
    private String resetBaseUrl;   // 재설정 페이지 기본 URL


    @Transactional
    public void requestPasswordReset(String email) {
        memberMapper.findByEmail(email).ifPresent(member -> {
            // 관리자 계정만 재설정 허용
            String roleName = memberMapper.findRoleNameById(member.getRoleId());
            if (!"ADMIN".equals(roleName) && !"SUPER_ADMIN".equals(roleName)) {
                return;   // 관리자가 아니면 아무것도 안 함 (조용히 무시)
            }

            passwordResetTokenMapper.deleteByMemberId(member.getMemberId());

            String token = java.util.UUID.randomUUID().toString();
            PasswordResetToken prt = PasswordResetToken.builder()
                    .memberId(member.getMemberId())
                    .token(token)
                    .expiresAt(java.time.LocalDateTime.now().plusMinutes(30))
                    .build();
            passwordResetTokenMapper.insert(prt);

            String resetLink = resetBaseUrl + "?token=" + token;
            mailService.sendPasswordResetMail(member.getEmail(), resetLink);
        });
        // 관리자가 아니거나 없는 이메일이어도 동일하게 정상 응답
    }


    @Transactional
    public void confirmPasswordReset(String token, String newPassword) {
        PasswordResetToken prt = passwordResetTokenMapper.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 링크입니다."));

        if (prt.getUsed() == 1) {
            throw new IllegalArgumentException("이미 사용된 링크입니다.");
        }
        if (prt.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("만료된 링크입니다. 다시 요청해 주세요.");
        }

        // 비밀번호 변경 (BCrypt 암호화)
        String encoded = passwordEncoder.encode(newPassword);
        memberMapper.updatePassword(prt.getMemberId(), encoded);

        // 토큰 사용 처리
        passwordResetTokenMapper.markUsed(token);
    }

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
    public void update(Long memberId, MemberUpdateDto dto) {
        memberMapper.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 비밀번호 암호화
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            dto.encodePassword(passwordEncoder);
        }

        memberMapper.update(memberId, dto);
    }

    @Transactional
    public void delete(Long memberId) {
        memberMapper.findById(memberId)  // findByLoginId → findById
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        memberMapper.delete(memberId);
    }

    // 아이디 중복체크 (true = 사용가능, false = 중복)
    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return memberMapper.findByLoginId(loginId).isEmpty();
    }

    // 로그인
    @Transactional
    public String login(MemberLoginDto dto) {
        Member member = memberMapper.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!member.getStatus().equals("ACTIVE")) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        // ── 기존 기기에 강제 로그아웃 푸시 (새 토큰 발급 전) ──
        sseService.forceLogout(member.getMemberId());

        String roleName = memberMapper.findRoleNameById(member.getRoleId());
        Long merchantId = null;
        if ("MERCHANT".equals(roleName)) {
            merchantId = merchantMapper.findMerchantIdByMemberId(member.getMemberId());
        }
        String token = jwtUtil.generateToken(member.getMemberId(), member.getLoginId(), roleName, merchantId);
        tokenMapper.upsert(member.getMemberId(), token);

        return token;
    }

    // MemberService 로그아웃 메서드
    @Transactional
    public void logout(Long memberId) {
        tokenMapper.deleteByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public String getNameByLoginId(String loginId) {
        return memberMapper.findByLoginId(loginId)
                .map(Member::getName)
                .orElse(loginId);
    }

    // ── 수혜자 비밀번호 초기화 (고유카드번호 + !) ──────────────
    @Transactional
    public void resetPasswordForMember(Long memberId) {
        memberMapper.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 고유카드(isPrimary=1) 번호 조회
        String cardNumber = cardMapper.findPrimaryCardNumberByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 고유카드가 없습니다."));

        String newPassword = passwordEncoder.encode(cardNumber + "!");
        memberMapper.updatePassword(memberId, newPassword);
    }

    // ── 가맹점 비밀번호 초기화 (사업자번호 + !) ─────────────────
    @Transactional
    public void resetPasswordForMerchant(Long memberId) {
        memberMapper.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 가맹점 사업자번호 조회
        String businessNumber = merchantMapper.findByMemberId(memberId)
                .map(m -> m.getBusinessNumber())
                .orElseThrow(() -> new IllegalArgumentException("연결된 가맹점이 없습니다."));

        String newPassword = passwordEncoder.encode(businessNumber + "!");
        memberMapper.updatePassword(memberId, newPassword);
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> getAllList(MemberSearchDto searchDto) {
        return memberMapper.findAllNoPaging(searchDto).stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
    }
}