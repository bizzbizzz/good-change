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
            "/api/merchants",
            "/api/settlements",        // 추가
            "/api/payments",           // 추가
            "/api/allowed-ips",
            "/api/site-config",
            "/api/stats",
            "/api/boards",
            "/api/logs",
            "/swagger-ui",
            "/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더 또는 URL 파라미터에서 토큰 추출
        String token = null;
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);  // 헤더에서 추출
        } else {
            token = request.getParameter("token");  // SSE용 URL 파라미터
        }

        if (token == null) {
            sendError(response, "토큰이 없습니다.");
            return;
        }

        if (!jwtUtil.validateToken(token)) {
            sendError(response, "유효하지 않은 토큰입니다.");
            return;
        }

        request.setAttribute("memberId", jwtUtil.getMemberId(token));
        request.setAttribute("loginId",  jwtUtil.getLoginId(token));

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}