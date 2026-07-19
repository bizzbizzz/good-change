package org.best.backspringboot.memberTest;

import org.best.backspringboot.commonDTO.PageResponse;
import org.best.backspringboot.commonDTO.SearchBase;
import org.best.backspringboot.member.dto.member.MemberCreateDto;
import org.best.backspringboot.member.dto.member.MemberLoginDto;
import org.best.backspringboot.member.dto.member.MemberResponseDto;
import org.best.backspringboot.member.dto.member.MemberUpdateDto;
import org.best.backspringboot.member.entity.Member;
import org.best.backspringboot.member.mapper.MemberMapper;
import org.best.backspringboot.merchant.mapper.MerchantMapper;
import org.best.backspringboot.member.service.MemberService;
import org.best.backspringboot.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
class MemberServiceTest {

    @Mock MemberMapper memberMapper;
    @Mock MerchantMapper merchantMapper;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;

    @InjectMocks MemberService memberService;

    // ── 테스트용 Member 픽스처 ─────────────────────────
    private Member mockMember() {
        Member m = new Member();
        try {
            var f = Member.class;
            setField(m, "memberId",  1L);
            setField(m, "loginId",   "testuser");
            setField(m, "password",  "encoded_pw");
            setField(m, "name",      "홍길동");
            setField(m, "roleId",    2L);
            setField(m, "status",    "ACTIVE");
            setField(m, "point",     100000L);
        } catch (Exception ignored) {}
        return m;
    }

    private void setField(Object obj, String name, Object val) throws Exception {
        var field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, val);
    }

    // ── CREATE ────────────────────────────────────────
    @Test
    @DisplayName("회원 등록 성공")
    void create_success() throws Exception {
        given(memberMapper.findByLoginId("newuser")).willReturn(Optional.empty());

        MemberCreateDto dto = new MemberCreateDto();
        setField(dto, "loginId", "newuser"); // ✅ 추가

        assertThatNoException().isThrownBy(() -> memberService.create(dto));
        then(memberMapper).should().insert(dto);
    }

    @Test
    @DisplayName("회원 등록 실패 - 아이디 중복")
    void create_duplicateLoginId() throws Exception {
        given(memberMapper.findByLoginId("testuser")).willReturn(Optional.of(mockMember()));

        MemberCreateDto dto = new MemberCreateDto();
        // ✅ loginId 세팅 필요
        setField(dto, "loginId", "testuser");

        assertThatThrownBy(() -> memberService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용 중인 아이디");
    }

    // ── READ ──────────────────────────────────────────
    @Test
    @DisplayName("회원 단건 조회 성공")
    void getById_success() {
        given(memberMapper.findById(1L)).willReturn(Optional.of(mockMember()));
        MemberResponseDto result = memberService.getById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("회원 단건 조회 실패 - 없는 회원")
    void getById_notFound() {
        given(memberMapper.findById(999L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> memberService.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("회원 전체 조회 (페이징)")
    void getAll_success() {
        SearchBase searchBase = new SearchBase();
        given(memberMapper.findAll(searchBase)).willReturn(List.of(mockMember()));
        given(memberMapper.countAll()).willReturn(1L);

        PageResponse<MemberResponseDto> result = memberService.getAll(searchBase);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalCount()).isEqualTo(1L);
    }

    // ── UPDATE ────────────────────────────────────────
    @Test
    @DisplayName("회원 수정 성공")
    void update_success() {
        given(memberMapper.findByLoginId("testuser")).willReturn(Optional.of(mockMember()));
        MemberUpdateDto dto = new MemberUpdateDto();
        assertThatNoException().isThrownBy(() -> memberService.update("testuser", dto));
        then(memberMapper).should().update("testuser", dto);
    }

    @Test
    @DisplayName("회원 수정 실패 - 없는 회원")
    void update_notFound() {
        given(memberMapper.findByLoginId("none")).willReturn(Optional.empty());
        assertThatThrownBy(() -> memberService.update("none", new MemberUpdateDto()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── DELETE ────────────────────────────────────────
    @Test
    @DisplayName("회원 삭제 성공")
    void delete_success() {
        given(memberMapper.findByLoginId("testuser")).willReturn(Optional.of(mockMember()));
        assertThatNoException().isThrownBy(() -> memberService.delete("testuser"));
        then(memberMapper).should().delete("testuser");
    }

    // ── 중복체크 ──────────────────────────────────────
    @Test
    @DisplayName("아이디 중복체크 - 사용가능")
    void isLoginIdAvailable_available() {
        given(memberMapper.findByLoginId("newuser")).willReturn(Optional.empty());
        assertThat(memberService.isLoginIdAvailable("newuser")).isTrue();
    }

    @Test
    @DisplayName("아이디 중복체크 - 중복")
    void isLoginIdAvailable_duplicate() {
        given(memberMapper.findByLoginId("testuser")).willReturn(Optional.of(mockMember()));
        assertThat(memberService.isLoginIdAvailable("testuser")).isFalse();
    }

    // ── 로그인 ────────────────────────────────────────
    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        MemberLoginDto dto = new MemberLoginDto();
        setField(dto, "loginId", "testuser");   // ✅ 추가
        setField(dto, "password", "test1234!"); // ✅ 추가

        given(memberMapper.findByLoginId("testuser")).willReturn(Optional.of(mockMember()));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(memberMapper.findRoleNameById(2L)).willReturn("USER");
        given(jwtUtil.generateToken(anyLong(), anyString(), anyString(), any())).willReturn("token");

        String token = memberService.login(dto);
        assertThat(token).isEqualTo("token");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_wrongPassword() throws Exception {
        MemberLoginDto dto = new MemberLoginDto();
        setField(dto, "loginId", "testuser");   // ✅ 추가
        setField(dto, "password", "wrongpw!");  // ✅ 추가

        given(memberMapper.findByLoginId("testuser")).willReturn(Optional.of(mockMember()));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        assertThatThrownBy(() -> memberService.login(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호");
    }

    @Test
    @DisplayName("로그인 실패 - 비활성화 계정")
    void login_inactiveAccount() throws Exception {
        MemberLoginDto dto = new MemberLoginDto();
        setField(dto, "loginId", "testuser");   // ✅ 추가
        setField(dto, "password", "test1234!"); // ✅ 추가

        Member inactive = mockMember();
        setField(inactive, "status", "DELETED");
        given(memberMapper.findByLoginId("testuser")).willReturn(Optional.of(inactive));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        assertThatThrownBy(() -> memberService.login(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비활성화");
    }
}
