package org.best.backspringboot.controller;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.service.SseService;
import org.best.backspringboot.util.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final JwtUtil jwtUtil;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam("token") String token) {
        // SSE는 헤더 못 보내서 쿼리파라미터로 토큰 받음
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰");
        }
        Long memberId = jwtUtil.getMemberId(token);
        return sseService.connect(memberId);
    }
}