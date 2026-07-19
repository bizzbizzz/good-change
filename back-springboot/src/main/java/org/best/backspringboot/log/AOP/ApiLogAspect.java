package org.best.backspringboot.log.AOP;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.best.backspringboot.log.entity.ApiLog;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class ApiLogAspect {

    private final MongoTemplate mongoTemplate;

    // 모든 Controller 메서드에 적용
    @Around("execution(* org.best.backspringboot.controller..*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        HttpServletRequest request = getRequest();

        // 요청 정보
        String method  = request != null ? request.getMethod() : "UNKNOWN";
        String url     = request != null ? request.getRequestURI() : "UNKNOWN";
        Long memberId  = request != null ? (Long) request.getAttribute("memberId") : null;
        String loginId = request != null ? (String) request.getAttribute("loginId") : null;
        String clientIp = getClientIp(request);  // ✅ 추가


        // 파라미터
        String requestBody = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg != null)
                .map(arg -> {
                    try {
                        return arg.toString();
                    } catch (Exception e) {
                        return "?";
                    }
                })
                .collect(java.util.stream.Collectors.joining(", "));


        Object result;
        String level        = "INFO";
        String errorMessage = null;
        int    responseCode = 200;

        try {
            result = joinPoint.proceed();
        } catch (IllegalArgumentException e) {
            level        = "WARNING";
            errorMessage = e.getMessage();
            responseCode = 400;
            saveLog(method, url, memberId, loginId, clientIp, requestBody,
                    level, responseCode, errorMessage,
                    System.currentTimeMillis() - startTime, joinPoint);
            throw e;
        } catch (Exception e) {
            level        = "ERROR";
            errorMessage = e.getMessage();
            responseCode = 500;
            saveLog(method, url, memberId, loginId, clientIp, requestBody,
                    level, responseCode, errorMessage,
                    System.currentTimeMillis() - startTime, joinPoint);
            throw e;
        }

        saveLog(method, url, memberId, loginId, clientIp, requestBody,
                level, responseCode, errorMessage,
                System.currentTimeMillis() - startTime, joinPoint);

        return result;
    }

    private void saveLog(String method, String url,
                         Long memberId, String loginId,
                         String clientIp,
                         String requestBody, String level,
                         int responseCode, String errorMessage,
                         long elapsedTime, ProceedingJoinPoint joinPoint) {
        try {
            String action = resolveAction(method, url, joinPoint.getSignature().getName());

            ApiLog log = ApiLog.builder()
                    .source("api")
                    .timestamp(LocalDateTime.now())
                    .level(level)
                    .memberId(memberId)
                    .loginId(loginId)
                    .method(method)
                    .url(url)
                    .action(action)
                    .clientIp(clientIp)  // ✅ 추가
                    .requestBody(requestBody)
                    .responseCode(responseCode)
                    .elapsedTime(elapsedTime)
                    .errorMessage(errorMessage)
                    .build();

            mongoTemplate.insert(log);
        } catch (Exception e) {
            // 로그 저장 실패 시 무시 (서비스에 영향 없도록)
            System.err.println("[ApiLogAspect] 로그 저장 실패: " + e.getMessage());
        }
    }

    // URL + 메서드명으로 action 결정
    private String resolveAction(String httpMethod, String url, String methodName) {
        if (url.contains("/payments")) {
            if (url.contains("/cancel"))  return "결제 취소";
            if ("POST".equals(httpMethod)) return "결제 요청";
            if ("DELETE".equals(httpMethod)) return "결제내역 삭제";
            return "결제내역 조회";
        }
        if (url.contains("/members")) {
            if (url.contains("/login"))    return "로그인";
            if ("POST".equals(httpMethod)) return "회원 등록";
            if ("PATCH".equals(httpMethod)) return "회원 수정";
            if ("DELETE".equals(httpMethod)) return "회원 삭제";
            return "회원 조회";
        }
        if (url.contains("/merchants")) {
            if ("POST".equals(httpMethod)) return "가맹점 등록";
            if ("PATCH".equals(httpMethod)) return "가맹점 수정";
            if ("DELETE".equals(httpMethod)) return "가맹점 삭제";
            return "가맹점 조회";
        }
        if (url.contains("/cards")) {
            if ("POST".equals(httpMethod)) return "카드 등록";
            if ("DELETE".equals(httpMethod)) return "카드 삭제";
            return "카드 조회";
        }
        if (url.contains("/settlements")) {
            if (url.contains("/status")) return "정산 상태 변경";
            return "정산 조회";
        }
        return methodName;
    }

    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For는 여러 IP가 있을 수 있어서 첫번째만
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
