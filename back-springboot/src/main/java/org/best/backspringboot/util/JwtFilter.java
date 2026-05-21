package org.best.backspringboot.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // 인증 없이 통과할 경로
    private static final List<String> WHITE_LIST = List.of(
            "/api/members/login",
            "/api/members/check-id",
            "/api/members",          // 회원가입
            "/api/cards",            // 카드 조회
            "/swagger-ui",
            "/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 화이트리스트 경로는 토큰 검사 패스
        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더 추출
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, "토큰이 없습니다.");
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            sendError(response, "유효하지 않은 토큰입니다.");
            return;
        }

        // 토큰에서 꺼낸 정보를 request attribute에 담아서 컨트롤러에서 사용 가능
        request.setAttribute("memberId", jwtUtil.getMemberId(token));
        request.setAttribute("loginId", jwtUtil.getLoginId(token));

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}