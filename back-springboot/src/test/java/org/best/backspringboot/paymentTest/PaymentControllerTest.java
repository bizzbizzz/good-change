package org.best.backspringboot.paymentTest;

import org.best.backspringboot.controller.PaymentController;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.payment.PaymentResponseDto;
import org.best.backspringboot.exception.GlobalExceptionHandler;
import org.best.backspringboot.service.PaymentService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = PaymentController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@Import(GlobalExceptionHandler.class)
@DisplayName("PaymentController API 테스트")
class PaymentControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean PaymentService paymentService;
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

    private PaymentResponseDto mockPaymentResponse() {
        return PaymentResponseDto.builder()
                .paymentId(1L)
                .cardId(1L)
                .cardNumber("1234567890123456")
                .merchantName("테스트가맹점")
                .amount(50000L)
                .originalAmount(100000L)
                .remainingPoint(50000L)
                .transactionType("사용")
                .approvalNumber("123456789012")
                .responseCode("0000")
                .status("SUCCESS")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ── POST /api/payments ────────────────────────────
    @Test
    @DisplayName("POST /api/payments - 결제 성공")
    void pay_success() throws Exception {
        given(paymentService.pay(any())).willReturn(mockPaymentResponse());

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "cardNumber": "1234567890123456",
                                    "amount": 50000
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.amount").value(50000))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionType").value("사용"))
                .andExpect(jsonPath("$.remainingPoint").value(50000));
    }

    @Test
    @DisplayName("POST /api/payments - 카드번호 미입력 시 400")
    void pay_missingCardNumber() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "amount": 50000
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/payments - 금액 0 이하 시 400")
    void pay_invalidAmount() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "cardNumber": "1234567890123456",
                                    "amount": 0
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/payments - 포인트 부족 시 400")
    void pay_insufficientPoint() throws Exception {
        given(paymentService.pay(any()))
                .willThrow(new IllegalArgumentException("보유 포인트가 부족합니다."));

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "cardNumber": "1234567890123456",
                                    "amount": 999999999
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/payments - 존재하지 않는 카드 시 400")
    void pay_cardNotFound() throws Exception {
        given(paymentService.pay(any()))
                .willThrow(new IllegalArgumentException("존재하지 않는 카드번호입니다."));

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "cardNumber": "9999999999999999",
                                    "amount": 50000
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/payments ─────────────────────────────
    @Test
    @DisplayName("GET /api/payments - 전체 조회 (페이징)")
    void getAll_success() throws Exception {
        PageResponse<PaymentResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(1);
        pageResponse.setSize(10);
        pageResponse.setPageInfo(List.of(mockPaymentResponse()), 1L);

        given(paymentService.getAll(any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/payments")
                        .header("Authorization", generateToken())
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].paymentId").value(1))
                .andExpect(jsonPath("$.totalCount").value(1));
    }

    @Test
    @DisplayName("GET /api/payments - 날짜 범위 검색")
    void getAll_withDateFilter() throws Exception {
        PageResponse<PaymentResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(1);
        pageResponse.setSize(10);
        pageResponse.setPageInfo(List.of(mockPaymentResponse()), 1L);

        given(paymentService.getAll(any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/payments")
                        .header("Authorization", generateToken())
                        .param("startDate", "20260501")
                        .param("endDate", "20260531"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ── GET /api/payments/{paymentId} ─────────────────
    @Test
    @DisplayName("GET /api/payments/{paymentId} - 단건 조회 성공")
    void getById_success() throws Exception {
        given(paymentService.getById(1L)).willReturn(mockPaymentResponse());

        mockMvc.perform(get("/api/payments/1")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.amount").value(50000))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("GET /api/payments/{paymentId} - 없는 결제 시 400")
    void getById_notFound() throws Exception {
        given(paymentService.getById(999L))
                .willThrow(new IllegalArgumentException("존재하지 않는 결제내역입니다."));

        mockMvc.perform(get("/api/payments/999")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── PATCH /api/payments/{paymentId}/cancel ────────
    @Test
    @DisplayName("PATCH /api/payments/{paymentId}/cancel - 결제 취소 성공")
    void cancel_success() throws Exception {
        willDoNothing().given(paymentService).cancel(1L);

        mockMvc.perform(patch("/api/payments/1/cancel")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/payments/{paymentId}/cancel - 이미 취소된 결제 시 400")
    void cancel_alreadyCanceled() throws Exception {
        willThrow(new IllegalArgumentException("취소 가능한 결제가 아닙니다."))
                .given(paymentService).cancel(1L);

        mockMvc.perform(patch("/api/payments/1/cancel")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/payments/{paymentId}/cancel - 15일 초과 취소 시 400")
    void cancel_expired() throws Exception {
        willThrow(new IllegalArgumentException("결제 후 15일이 초과하여 취소할 수 없습니다."))
                .given(paymentService).cancel(1L);

        mockMvc.perform(patch("/api/payments/1/cancel")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /api/payments/{paymentId} ──────────────
    @Test
    @DisplayName("DELETE /api/payments/{paymentId} - 결제내역 삭제 성공")
    void delete_success() throws Exception {
        willDoNothing().given(paymentService).delete(1L);

        mockMvc.perform(delete("/api/payments/1")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/payments/{paymentId} - 없는 결제내역 삭제 시 400")
    void delete_notFound() throws Exception {
        willThrow(new IllegalArgumentException("존재하지 않는 결제내역입니다."))
                .given(paymentService).delete(999L);

        mockMvc.perform(delete("/api/payments/999")
                        .header("Authorization", generateToken()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
