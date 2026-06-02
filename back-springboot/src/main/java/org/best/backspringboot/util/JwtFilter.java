package org.best.backspringboot.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.mapper.TokenMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenMapper tokenMapper;

    // 토큰 검증 자체를 건너뛸 경로 (로그인/회원가입/문서)
    private static final List<String> SKIP_LIST = List.of(
            "/api/members/login",
            "/api/members/check-id",
            "/swagger-ui",
            "/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. 완전 통과 경로는 그냥 넘김
        if (SKIP_LIST.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 토큰 추출
        String token = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            token = request.getParameter("token");
        }

        // 3. 토큰이 있으면 검증 + SecurityContext 세팅
        if (token != null && jwtUtil.validateToken(token)) {
            Long memberId = jwtUtil.getMemberId(token);
            String savedToken = tokenMapper.findByMemberId(memberId);

            // 중복 로그인 체크 — DB 토큰과 일치할 때만 인증 처리
            if (savedToken != null && savedToken.equals(token)) {
                String role = jwtUtil.getRole(token);   // SUPER_ADMIN, ADMIN, USER, MERCHANT

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                memberId,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("memberId", memberId);
                request.setAttribute("loginId", jwtUtil.getLoginId(token));
            }
        }

        // 4. 토큰이 없거나 검증 실패해도 일단 통과시킴
        //    → 권한 차단은 @PreAuthorize가 담당
        //    → 공개 API(@PreAuthorize 없음)는 그대로 동작
        //    → 인증 필요한 API(@PreAuthorize 있음)는 SecurityContext 비어있으면 거부됨
        filterChain.doFilter(request, response);
    }
}