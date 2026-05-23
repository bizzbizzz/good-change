package org.best.backspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.statistics.DailyPaymentDto;
import org.best.backspringboot.dto.statistics.MonthlySettlementDto;
import org.best.backspringboot.service.StatsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Tag(name = "통계", description = "통계 API")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // ── 일반 REST API ──────────────────────────────────────────

    @Operation(summary = "일별 결제금액")
    @GetMapping("/daily-payment")
    public ResponseEntity<List<DailyPaymentDto>> getDailyPayment(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(statsService.getDailyPayment(days));
    }

    @Operation(summary = "월별 정산금액")
    @GetMapping("/monthly-settlement")
    public ResponseEntity<List<MonthlySettlementDto>> getMonthlySettlement(
            @RequestParam(defaultValue = "12") int months) {
        return ResponseEntity.ok(statsService.getMonthlySettlement(months));
    }

    // ── SSE ───────────────────────────────────────────────────

    @Operation(summary = "통계 SSE 스트림")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(60_000L); // 60초 타임아웃

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                // 1. 일별 결제금액
                List<DailyPaymentDto> daily = statsService.getDailyPayment(30);
                emitter.send(SseEmitter.event()
                        .name("daily-payment")
                        .data(objectMapper.writeValueAsString(daily)));

                // 2. 월별 정산금액
                List<MonthlySettlementDto> monthly = statsService.getMonthlySettlement(12);
                emitter.send(SseEmitter.event()
                        .name("monthly-settlement")
                        .data(objectMapper.writeValueAsString(monthly)));

                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("done"));

                emitter.complete();

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    // ── 주기적 갱신 SSE (30초마다) ────────────────────────────

    @Operation(summary = "통계 실시간 SSE (30초 갱신)")
    @GetMapping(value = "/stream/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLive() {
        SseEmitter emitter = new SseEmitter(300_000L); // 5분

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (true) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("dailyPayment",       statsService.getDailyPayment(30));
                    data.put("monthlySettlement",  statsService.getMonthlySettlement(12));

                    emitter.send(SseEmitter.event()
                            .name("stats")
                            .data(objectMapper.writeValueAsString(data)));

                    Thread.sleep(30_000); // 30초마다 갱신
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
