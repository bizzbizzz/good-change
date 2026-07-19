package org.best.backspringboot.paymentTest;

import org.best.backspringboot.payment.controller.PaymentController;
import org.best.backspringboot.exception.GlobalExceptionHandler;
import org.best.backspringboot.payment.service.PaymentService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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

}
