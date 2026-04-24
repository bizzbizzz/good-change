package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.member.MemberCreateDto;
import org.best.backspringboot.dto.member.MemberLoginDto;
import org.best.backspringboot.dto.member.MemberResponseDto;
import org.best.backspringboot.dto.member.MemberUpdateDto;
import org.best.backspringboot.entity.Member;
import org.best.backspringboot.mapper.MemberMapper;
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
    private final JwtUtil jwtUtil;

    @Transactional
    public void create(MemberCreateDto dto) {
        // 아이디 중복 체크
        memberMapper.findByLoginId(dto.getLoginId())
                .ifPresent(m -> { throw new IllegalArgumentException("이미 사용 중인 아이디입니다."); });
        // 비밀번호 암호화
        dto.encodePassword(passwordEncoder);
        dto.encodePhone(passwordEncoder);
        memberMapper.insert(dto);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto getById(Long memberId) {
        return memberMapper.findById(memberId)
                .map(MemberResponseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> getAll() {
        return memberMapper.findAll().stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
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

        return jwtUtil.generateToken(member.getMemberId(), member.getLoginId());
    }
}