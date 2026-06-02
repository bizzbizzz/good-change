package org.best.backspringboot.memberTest;

import org.best.backspringboot.controller.MemberController;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.member.MemberResponseDto;
import org.best.backspringboot.exception.GlobalExceptionHandler;
import org.best.backspringboot.service.MemberService;
import org.best.backspringboot.util.JwtFilter;
import org.best.backspringboot.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MemberController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@Import(GlobalExceptionHandler.class)
@DisplayName("MemberController API 테스트")
class MemberControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean MemberService memberService;
    @MockitoBean JwtUtil jwtUtil;
    @MockitoBean JwtFilter jwtFilter;

    @BeforeEach
    void setUp() throws Exception {
        willAnswer(invocation -> {
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).given(jwtFilter).doFilter(any(), any(), any());

        given(jwtUtil.validateToken(anyString())).willReturn(true);
        given(jwtUtil.getMemberId(anyString())).willReturn(1L);
        given(jwtUtil.getLoginId(anyString())).willReturn("testuser");
    }

    private String generateToken() {
        JwtUtil realJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(realJwtUtil, "secret",
                "goodchange-secret-key-must-be-at-least-32-chars!!");
        ReflectionTestUtils.setField(realJwtUtil, "expiration", 3600000L);
        return "Bearer " + realJwtUtil.generateToken(1L, "testuser", "USER", null);
    }

    private MemberResponseDto mockMemberResponse() {
        return MemberResponseDto.builder()
                .memberId(1L)
                .loginId("testuser")
                .name("홍길동")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender("MALE")
                .phone("01012345678")
                .address("서울시 강남구")
                .status("ACTIVE")
                .point(100000L)
                .roleId(2L)
                .build();
    }

    // ── POST /api/members ─────────────────────────────
    @Test
    @DisplayName("POST /api/members - 회원 등록 성공")
    void create_success() throws Exception {
        willDoNothing().given(memberService).create(any());

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "testuser",
                                    "password": "Test1234!",
                                    "name": "홍길동",
                                    "birthDate": "1990-01-01",
                                    "gender": "MALE",
                                    "phone": "01012345678",
                                    "address": "서울시 강남구"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/members - 필수값 누락 시 400")
    void create_missingRequired() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "testuser"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/members - 비밀번호 형식 오류 시 400")
    void create_invalidPassword() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "testuser",
                                    "password": "1234",
                                    "name": "홍길동",
                                    "birthDate": "1990-01-01",
                                    "gender": "MALE",
                                    "phone": "01012345678",
                                    "address": "서울시 강남구"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/members - 아이디 중복 시 400")
    void create_duplicateLoginId() throws Exception {
        willThrow(new IllegalArgumentException("이미 사용 중인 아이디입니다."))
                .given(memberService).create(any());

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "testuser",
                                    "password": "Test1234!",
                                    "name": "홍길동",
                                    "birthDate": "1990-01-01",
                                    "gender": "MALE",
                                    "phone": "01012345678",
                                    "address": "서울시 강남구"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── POST /api/members/login ───────────────────────
    @Test
    @DisplayName("POST /api/members/login - 로그인 성공")
    void login_success() throws Exception {
        given(memberService.login(any())).willReturn("mocked.jwt.token");

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "testuser",
                                    "password": "Test1234!"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"));
    }

    @Test
    @DisplayName("POST /api/members/login - 비밀번호 불일치 시 400")
    void login_wrongPassword() throws Exception {
        willThrow(new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."))
                .given(memberService).login(any());

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "testuser",
                                    "password": "Wrong1234!"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/members/check-id ─────────────────────
    @Test
    @DisplayName("GET /api/members/check-id - 사용가능 아이디")
    void checkLoginId_available() throws Exception {
        given(memberService.isLoginIdAvailable("newuser")).willReturn(true);

        mockMvc.perform(get("/api/members/check-id")
                        .param("loginId", "newuser"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/members/check-id - 중복 아이디")
    void checkLoginId_duplicate() throws Exception {
        given(memberService.isLoginIdAvailable("testuser")).willReturn(false);

        mockMvc.perform(get("/api/members/check-id")
                        .param("loginId", "testuser"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ── GET /api/members/{memberId} ───────────────────
    @Test
    @DisplayName("GET /api/members/{memberId} - 단건 조회 성공")
    void getById_success() throws Exception {
        given(memberService.getById(1L)).willReturn(mockMemberResponse());

        mockMvc.perform(get("/api/members/1")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.loginId").value("testuser"))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/members/{memberId} - 없는 회원 시 400")
    void getById_notFound() throws Exception {
        given(memberService.getById(999L))
                .willThrow(new IllegalArgumentException("존재하지 않는 회원입니다."));

        mockMvc.perform(get("/api/members/999")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/members ──────────────────────────────
    @Test
    @DisplayName("GET /api/members - 전체 조회 (페이징)")
    void getAll_success() throws Exception {
        PageResponse<MemberResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(1);
        pageResponse.setSize(10);
        pageResponse.setPageInfo(List.of(mockMemberResponse()), 1L);

        given(memberService.getAll(any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/members")
                        .header("Authorization", generateToken())
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].memberId").value(1))
                .andExpect(jsonPath("$.totalCount").value(1));
    }

    // ── PATCH /api/members/{loginId} ──────────────────
    @Test
    @DisplayName("PATCH /api/members/{loginId} - 회원 수정 성공")
    void update_success() throws Exception {
        willDoNothing().given(memberService).update(anyString(), any());

        mockMvc.perform(patch("/api/members/testuser")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "김철수",
                                    "phone": "01099998888"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/members/{loginId} - 없는 회원 수정 시 400")
    void update_notFound() throws Exception {
        willThrow(new IllegalArgumentException("존재하지 않는 회원입니다."))
                .given(memberService).update(anyString(), any());

        mockMvc.perform(patch("/api/members/nobody")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "김철수"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /api/members/{loginId} ─────────────────
    @Test
    @DisplayName("DELETE /api/members/{loginId} - 회원 삭제 성공")
    void delete_success() throws Exception {
        willDoNothing().given(memberService).delete("testuser");

        mockMvc.perform(delete("/api/members/testuser")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/members/{loginId} - 없는 회원 삭제 시 400")
    void delete_notFound() throws Exception {
        willThrow(new IllegalArgumentException("존재하지 않는 회원입니다."))
                .given(memberService).delete("nobody");

        mockMvc.perform(delete("/api/members/nobody")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
