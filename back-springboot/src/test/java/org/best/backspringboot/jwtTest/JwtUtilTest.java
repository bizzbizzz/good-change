package org.best.backspringboot.jwtTest;

import org.best.backspringboot.global.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtUtil 테스트")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "goodchange-secret-key-must-be-at-least-32-chars!!");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    @DisplayName("토큰 생성 성공")
    void generateToken_success() {
        String token = jwtUtil.generateToken(1L, "testuser", "USER", null);
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("토큰에서 loginId 추출")
    void getLoginId_success() {
        String token = jwtUtil.generateToken(1L, "testuser", "USER", null);
        assertThat(jwtUtil.getLoginId(token)).isEqualTo("testuser");
    }

    @Test
    @DisplayName("토큰에서 memberId 추출")
    void getMemberId_success() {
        String token = jwtUtil.generateToken(1L, "testuser", "USER", null);
        assertThat(jwtUtil.getMemberId(token)).isEqualTo(1L);
    }

    @Test
    @DisplayName("토큰에서 role 추출")
    void getRole_success() {
        String token = jwtUtil.generateToken(1L, "testuser", "MERCHANT", 5L);
        assertThat(jwtUtil.getRole(token)).isEqualTo("MERCHANT");
    }

    @Test
    @DisplayName("토큰에서 merchantId 추출")
    void getMerchantId_success() {
        String token = jwtUtil.generateToken(1L, "testuser", "MERCHANT", 5L);
        assertThat(jwtUtil.getMerchantId(token)).isEqualTo(5L);
    }

    @Test
    @DisplayName("토큰 유효성 검사 - 유효")
    void validateToken_valid() {
        String token = jwtUtil.generateToken(1L, "testuser", "USER", null);
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("토큰 유효성 검사 - 무효")
    void validateToken_invalid() {
        assertThat(jwtUtil.validateToken("invalid.token.here")).isFalse();
    }
}
