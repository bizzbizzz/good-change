package org.best.backspringboot.cardTest;

import org.best.backspringboot.controller.CardController;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.card.CardResponseDto;
import org.best.backspringboot.service.CardService;
import org.best.backspringboot.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CardController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class  // ✅ Security 제외
)
@DisplayName("CardController API 테스트")
class CardControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    CardService cardService;
    @MockitoBean
    JwtUtil jwtUtil;  // ✅ 추가!

    // ── 픽스처 ────────────────────────────────────────
    private CardResponseDto mockCardResponse() {
        return CardResponseDto.builder()
                .cardId(1L)
                .memberId(1L)
                .cardNumber("1234567890123456")
                .cardAlias("나의 카드")
                .isPrimary(1)
                .status("ACTIVE")
                .memberName("홍길동")
                .point(100000L)
                .build();
    }

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "goodchange-secret-key-must-be-at-least-32-chars!!");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    // ── POST /api/cards ───────────────────────────────
    @Test
    @DisplayName("POST /api/cards - 카드 등록 성공")
    void create_success() throws Exception {
        willDoNothing().given(cardService).create(any());

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "memberId": 1,
                                    "cardNumber": "1234567890123456"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/cards - 카드번호 미입력 시 400")
    void create_missingCardNumber() throws Exception {
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "memberId": 1
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/cards - 카드번호 16자리 미만 시 400")
    void create_invalidCardNumber() throws Exception {
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "memberId": 1,
                                    "cardNumber": "12345"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/cards - 중복 카드번호 시 400")
    void create_duplicateCard() throws Exception {
        willThrow(new IllegalArgumentException("이미 등록된 카드번호입니다."))
                .given(cardService).create(any());

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "memberId": 1,
                                    "cardNumber": "1234567890123456"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/cards/{cardNumber} ───────────────────
    @Test
    @DisplayName("GET /api/cards/{cardNumber} - 카드번호로 조회 성공")
    void getByCardNumber_success() throws Exception {
        given(cardService.getByCardNumber("1234567890123456"))
                .willReturn(mockCardResponse());

        mockMvc.perform(get("/api/cards/1234567890123456"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(1))
                .andExpect(jsonPath("$.cardNumber").value("1234567890123456"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.memberName").value("홍길동"))
                .andExpect(jsonPath("$.point").value(100000));
    }

    @Test
    @DisplayName("GET /api/cards/{cardNumber} - 없는 카드 시 400")
    void getByCardNumber_notFound() throws Exception {
        given(cardService.getByCardNumber(anyString()))
                .willThrow(new IllegalArgumentException("존재하지 않는 카드번호입니다."));

        mockMvc.perform(get("/api/cards/9999999999999999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/cards/member/{memberId} ─────────────
    @Test
    @DisplayName("GET /api/cards/member/{memberId} - 회원별 카드 조회 성공")
    void getByMemberId_success() throws Exception {
        given(cardService.getByMemberId(1L))
                .willReturn(List.of(mockCardResponse()));

        mockMvc.perform(get("/api/cards/member/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cardId").value(1))
                .andExpect(jsonPath("$[0].memberId").value(1));
    }

    @Test
    @DisplayName("GET /api/cards/member/{memberId} - 카드 없으면 빈 배열")
    void getByMemberId_empty() throws Exception {
        given(cardService.getByMemberId(1L)).willReturn(List.of());

        mockMvc.perform(get("/api/cards/member/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /api/cards ────────────────────────────────
    @Test
    @DisplayName("GET /api/cards - 전체 조회 (페이징)")
    void getAll_success() throws Exception {
        PageResponse<CardResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(1);
        pageResponse.setSize(10);
        pageResponse.setPageInfo(List.of(mockCardResponse()), 1L);

        given(cardService.getAll(any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/cards")
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].cardId").value(1))
                .andExpect(jsonPath("$.totalCount").value(1));
    }

    // ── DELETE /api/cards/{cardId} ────────────────────
    @Test
    @DisplayName("DELETE /api/cards/{cardId} - 카드 삭제 성공")
    void delete_success() throws Exception {
        willDoNothing().given(cardService).delete(1L);

        mockMvc.perform(delete("/api/cards/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/cards/{cardId} - 없는 카드 삭제 시 400")
    void delete_notFound() throws Exception {
        willThrow(new IllegalArgumentException("존재하지 않는 카드입니다."))
                .given(cardService).delete(999L);

        mockMvc.perform(delete("/api/cards/999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
