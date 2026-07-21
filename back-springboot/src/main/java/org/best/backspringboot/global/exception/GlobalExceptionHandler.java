package org.best.backspringboot.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateKey(Exception e) {
        String message = "중복된 데이터가 존재합니다.";
        String msg = e.getMessage() != null ? e.getMessage() : "";

        if (msg.contains("card_number"))       message = "이미 등록된 카드번호입니다.";
        else if (msg.contains("login_id"))     message = "이미 사용중인 아이디입니다.";
        else if (msg.contains("business_number")) message = "이미 등록된 사업자번호입니다.";
        else if (msg.contains("terminal_id")) message = "이미 등록된 단말기ID입니다.";
        else if (msg.contains("ip_address"))  message = "이미 등록된 IP입니다.";

        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}