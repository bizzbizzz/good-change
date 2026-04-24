package org.best.backspringboot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Payment {
    private Long paymentId;
    private Long cardId;
    private String kocessIp;
    private String messageNumber;
    private String institutionCode;
    private String transmissionDate;
    private String traceNumber;
    private String terminalId;
    private String businessNumber;
    private String merchantNumber;
    private String merchantName;
    private String representative;
    private String phone;
    private String address;
    private String cardType;
    private String inputMethod;
    private String track;
    private Long amount;
    private String transactionType;
    private String approvalNumber;
    private String responseCode;
    private String cancelCode;
    private String originalTradeDate;
    private String originalApprovalNumber;
    private String acquirerCode;
    private String acquirerName;
    private String filterValue;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}