package org.best.backspringboot.settlementTest;

import org.best.backspringboot.settlement.controller.SettlementController;
import org.best.backspringboot.commonDTO.PageResponse;
import org.best.backspringboot.settlement.dto.settlement.SettlementResponseDto;
import org.best.backspringboot.exception.GlobalExceptionHandler;
import org.best.backspringboot.settlement.service.SettlementService;
import org.best.backspringboot.config.JwtFilter;
import org.best.backspringboot.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SettlementController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@Import(GlobalExceptionHandler.class)
@DisplayName("SettlementController API 테스트")
class SettlementControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean SettlementService settlementService;
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
        return "Bearer " + realJwtUtil.generateToken(1L, "testuser", "ADMIN", null);
    }

    private SettlementResponseDto mockSettlementResponse() {
        return SettlementResponseDto.builder()
                .settlementId(1L)
                .merchantId(1L)
                .merchantName("테스트가맹점")
                .businessNumber("1234567890")
                .loginId("merchant1")
                .settlementMonth("2026-05")
                .settlementAmount(500000L)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ── GET /api/settlements ──────────────────────────
    @Test
    @DisplayName("GET /api/settlements - 전체 조회 (페이징)")
    void getAll_success() throws Exception {
        PageResponse<SettlementResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(1);
        pageResponse.setSize(10);
        pageResponse.setPageInfo(List.of(mockSettlementResponse()), 1L);

        given(settlementService.getAll(any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/settlements")
                        .header("Authorization", generateToken())
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].settlementId").value(1))
                .andExpect(jsonPath("$.content[0].merchantName").value("테스트가맹점"))
                .andExpect(jsonPath("$.content[0].settlementAmount").value(500000))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.totalCount").value(1));
    }

    @Test
    @DisplayName("GET /api/settlements - 월 범위 검색")
    void getAll_withMonthFilter() throws Exception {
        PageResponse<SettlementResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(1);
        pageResponse.setSize(10);
        pageResponse.setPageInfo(List.of(mockSettlementResponse()), 1L);

        given(settlementService.getAll(any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/settlements")
                        .header("Authorization", generateToken())
                        .param("startMonth", "2026-01")
                        .param("endMonth", "2026-05"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/settlements - 상태 필터 검색 (PENDING)")
    void getAll_withStatusFilter() throws Exception {
        PageResponse<SettlementResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(1);
        pageResponse.setSize(10);
        pageResponse.setPageInfo(List.of(mockSettlementResponse()), 1L);

        given(settlementService.getAll(any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/settlements")
                        .header("Authorization", generateToken())
                        .param("status", "PENDING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/settlements - 가맹점명 검색")
    void getAll_withMerchantNameFilter() throws Exception {
        PageResponse<SettlementResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(1);
        pageResponse.setSize(10);
        pageResponse.setPageInfo(List.of(mockSettlementResponse()), 1L);

        given(settlementService.getAll(any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/settlements")
                        .header("Authorization", generateToken())
                        .param("merchantName", "테스트가맹점"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].merchantName").value("테스트가맹점"));
    }

    @Test
    @DisplayName("GET /api/settlements - 빈 결과")
    void getAll_empty() throws Exception {
        PageResponse<SettlementResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(1);
        pageResponse.setSize(10);
        pageResponse.setPageInfo(List.of(), 0L);

        given(settlementService.getAll(any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/settlements")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalCount").value(0));
    }

    // ── GET /api/settlements/{settlementId} ───────────
    @Test
    @DisplayName("GET /api/settlements/{settlementId} - 단건 조회 성공")
    void getById_success() throws Exception {
        given(settlementService.getById(1L)).willReturn(mockSettlementResponse());

        mockMvc.perform(get("/api/settlements/1")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.settlementId").value(1))
                .andExpect(jsonPath("$.merchantName").value("테스트가맹점"))
                .andExpect(jsonPath("$.settlementMonth").value("2026-05"))
                .andExpect(jsonPath("$.settlementAmount").value(500000))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/settlements/{settlementId} - 없는 정산 시 400")
    void getById_notFound() throws Exception {
        given(settlementService.getById(999L))
                .willThrow(new IllegalArgumentException("존재하지 않는 정산내역입니다."));

        mockMvc.perform(get("/api/settlements/999")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── PATCH /api/settlements/{settlementId}/status ──
    @Test
    @DisplayName("PATCH /api/settlements/{settlementId}/status - COMPLETED 변경 성공")
    void updateStatus_toCompleted() throws Exception {
        willDoNothing().given(settlementService).updateStatus(1L, "COMPLETED");

        mockMvc.perform(patch("/api/settlements/1/status")
                        .header("Authorization", generateToken())
                        .param("status", "COMPLETED"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/settlements/{settlementId}/status - FAILED 변경 성공")
    void updateStatus_toFailed() throws Exception {
        willDoNothing().given(settlementService).updateStatus(1L, "FAILED");

        mockMvc.perform(patch("/api/settlements/1/status")
                        .header("Authorization", generateToken())
                        .param("status", "FAILED"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/settlements/{settlementId}/status - DELETED 변경 성공")
    void updateStatus_toDeleted() throws Exception {
        willDoNothing().given(settlementService).updateStatus(1L, "DELETED");

        mockMvc.perform(patch("/api/settlements/1/status")
                        .header("Authorization", generateToken())
                        .param("status", "DELETED"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/settlements/{settlementId}/status - 없는 정산 상태 변경 시 400")
    void updateStatus_notFound() throws Exception {
        willThrow(new IllegalArgumentException("존재하지 않는 정산내역입니다."))
                .given(settlementService).updateStatus(999L, "COMPLETED");

        mockMvc.perform(patch("/api/settlements/999/status")
                        .header("Authorization", generateToken())
                        .param("status", "COMPLETED"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
