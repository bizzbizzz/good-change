package org.best.backspringboot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    // memberId → SseEmitter
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(Long memberId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

        // 먼저 새 emitter 등록 (기존 건 반환받음)
        SseEmitter old = emitters.put(memberId, emitter);

        // 기존 연결 정리
        if (old != null) {
            old.complete();
        }

        // key + value 둘 다 일치할 때만 제거 (새 emitter 보호)
        emitter.onCompletion(() -> emitters.remove(memberId, emitter));
        emitter.onTimeout(()    -> emitters.remove(memberId, emitter));
        emitter.onError(e       -> emitters.remove(memberId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            emitters.remove(memberId, emitter);
        }

        return emitter;
    }

    // 강제 로그아웃 푸시
    public void forceLogout(Long memberId) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("force-logout")
                        .data("다른 기기에서 로그인되었습니다."));
                emitter.complete();
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }
}