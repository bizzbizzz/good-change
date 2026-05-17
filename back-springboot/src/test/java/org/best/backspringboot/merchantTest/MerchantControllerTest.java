package org.best.backspringboot.merchantTest;

import org.best.backspringboot.controller.MerchantController;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.merchant.MerchantResponseDto;
import org.best.backspringboot.exception.GlobalExceptionHandler;
import org.best.backspringboot.service.MerchantService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MerchantController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@Import(GlobalExceptionHandler.class)
@DisplayName("MerchantController API 테스트")
class MerchantControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean MerchantService merchantService;
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
        return "Bearer " + realJwtUtil.generateToken(1L, "testuser", "MERCHANT", 1L);
    }

    private MerchantResponseDto mockMerchantResponse() {
        return MerchantResponseDto.builder()
                .merchantId(1L)
                .memberId(1L)
                .merchantName("테스트가맹점")
                .representative("홍길동")
                .businessNumber("1234567890")
                .contact("02-1234-5678")
                .address("서울시 강남구")
                .status("ACTIVE")
                .terminalId("TERM000001")
                .categories(List.of("음식점"))
                .build();
    }

    // ── POST /api/merchants ───────────────────────────
    @Test
    @DisplayName("POST /api/merchants - 가맹점 등록 성공")
    void create_success() throws Exception {
        willDoNothing().given(merchantService).create(any());

        mockMvc.perform(post("/api/merchants")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "memberId": 1,
                                    "merchantName": "테스트가맹점",
                                    "representative": "홍길동",
                                    "businessNumber": "1234567890"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/merchants - 필수값 누락 시 400")
    void create_missingRequired() throws Exception {
        mockMvc.perform(post("/api/merchants")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "merchantName": "테스트가맹점"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/merchants - 사업자번호 중복 시 400")
    void create_duplicateBusinessNumber() throws Exception {
        willThrow(new IllegalArgumentException("이미 등록된 사업자번호입니다."))
                .given(merchantService).create(any());

        mockMvc.perform(post("/api/merchants")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "memberId": 1,
                                    "merchantName": "테스트가맹점",
                                    "representative": "홍길동",
                                    "businessNumber": "1234567890"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/merchants/{merchantId} ──────────────
    @Test
    @DisplayName("GET /api/merchants/{merchantId} - 단건 조회 성공")
    void getById_success() throws Exception {
        given(merchantService.getById(1L)).willReturn(mockMerchantResponse());

        mockMvc.perform(get("/api/merchants/1")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchantId").value(1))
                .andExpect(jsonPath("$.merchantName").value("테스트가맹점"))
                .andExpect(jsonPath("$.businessNumber").value("1234567890"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.terminalId").value("TERM000001"));
    }

    @Test
    @DisplayName("GET /api/merchants/{merchantId} - 없는 가맹점 시 400")
    void getById_notFound() throws Exception {
        given(merchantService.getById(999L))
                .willThrow(new IllegalArgumentException("존재하지 않는 가맹점입니다."));

        mockMvc.perform(get("/api/merchants/999")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/merchants/member/{memberId} ──────────
    @Test
    @DisplayName("GET /api/merchants/member/{memberId} - 회원별 가맹점 조회 성공")
    void getByMemberId_success() throws Exception {
        given(merchantService.getByMemberId(1L)).willReturn(mockMerchantResponse());

        mockMvc.perform(get("/api/merchants/member/1")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchantId").value(1))
                .andExpect(jsonPath("$.memberId").value(1));
    }

    @Test
    @DisplayName("GET /api/merchants/member/{memberId} - 없는 회원 가맹점 시 400")
    void getByMemberId_notFound() throws Exception {
        given(merchantService.getByMemberId(999L))
                .willThrow(new IllegalArgumentException("해당 회원의 가맹점이 존재하지 않습니다."));

        mockMvc.perform(get("/api/merchants/member/999")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/merchants ────────────────────────────
    @Test
    @DisplayName("GET /api/merchants - 전체 조회 (페이징)")
    void getAll_success() throws Exception {
        PageResponse<MerchantResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(1);
        pageResponse.setSize(10);
        pageResponse.setPageInfo(List.of(mockMerchantResponse()), 1L);

        given(merchantService.getAll(any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/merchants")
                        .header("Authorization", generateToken())
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].merchantId").value(1))
                .andExpect(jsonPath("$.totalCount").value(1));
    }

    // ── PATCH /api/merchants/{merchantId} ─────────────
    @Test
    @DisplayName("PATCH /api/merchants/{merchantId} - 가맹점 수정 성공")
    void update_success() throws Exception {
        willDoNothing().given(merchantService).update(anyLong(), any());

        mockMvc.perform(patch("/api/merchants/1")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "merchantName": "수정된가맹점",
                                    "contact": "02-9999-8888"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/merchants/{merchantId} - 없는 가맹점 수정 시 400")
    void update_notFound() throws Exception {
        willThrow(new IllegalArgumentException("존재하지 않는 가맹점입니다."))
                .given(merchantService).update(anyLong(), any());

        mockMvc.perform(patch("/api/merchants/999")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "merchantName": "수정된가맹점"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /api/merchants/{merchantId} ────────────
    @Test
    @DisplayName("DELETE /api/merchants/{merchantId} - 가맹점 삭제 성공")
    void delete_success() throws Exception {
        willDoNothing().given(merchantService).delete(1L);

        mockMvc.perform(delete("/api/merchants/1")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/merchants/{merchantId} - 없는 가맹점 삭제 시 400")
    void delete_notFound() throws Exception {
        willThrow(new IllegalArgumentException("존재하지 않는 가맹점입니다."))
                .given(merchantService).delete(999L);

        mockMvc.perform(delete("/api/merchants/999")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
