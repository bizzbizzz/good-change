package org.best.backspringboot.log.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Builder
@Document(collection = "socket_logs")
public class ApiLog {

    @Id
    private String id;

    @Field("source")
    private String source;          // "api"

    @Field("timestamp")
    private LocalDateTime timestamp;

    @Field("level")
    private String level;           // INFO, WARNING, ERROR

    @Field("member_id")
    private Long memberId;

    @Field("login_id")
    private String loginId;

    @Field("method")
    private String method;          // GET, POST, PATCH, DELETE

    @Field("url")
    private String url;             // /api/payments

    @Field("action")
    private String action;          // 결제 성공, 회원 등록 등

    @Field("request_body")
    private String requestBody;

    @Field("response_code")
    private int responseCode;       // HTTP 상태코드

    @Field("elapsed_time")
    private long elapsedTime;       // 실행 시간 (ms)

    @Field("error_message")
    private String errorMessage;

    @Field("client_ip")
    private String clientIp;
}
