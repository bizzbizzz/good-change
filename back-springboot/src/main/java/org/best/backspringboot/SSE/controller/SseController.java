package org.best.backspringboot.SSE.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.SSE.service.SseService;
import org.best.backspringboot.global.util.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "SSE", description = "실시간 알림 SSE API")
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final JwtUtil jwtUtil;


    @Operation(
            summary = "SSE 연결",
            description = "회원별 실시간 이벤트 스트림에 연결합니다. " +
                    "SSE는 커스텀 헤더를 보낼 수 없어 토큰을 쿼리 파라미터로 전달받습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연결 성공 (text/event-stream)"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 토큰", content = @Content)
    })
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