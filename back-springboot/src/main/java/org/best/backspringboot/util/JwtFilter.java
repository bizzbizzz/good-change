package org.best.backspringboot.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.mapper.TokenMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenMapper tokenMapper;

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

        String token = null;
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            token = request.getParameter("token");
        }

        if (token == null) {
            sendError(response, "토큰이 없습니다.");
            return;
        }

        if (!jwtUtil.validateToken(token)) {
            sendError(response, "유효하지 않은 토큰입니다.");
            return;
        }

        // 중복 로그인 체크
        Long memberId = jwtUtil.getMemberId(token);
        String savedToken = tokenMapper.findByMemberId(memberId);
        if (savedToken == null || !savedToken.equals(token)) {
            sendError(response, "다른 기기에서 로그인되었습니다.");
            return;
        }

        request.setAttribute("memberId", memberId);
        request.setAttribute("loginId",  jwtUtil.getLoginId(token));

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}