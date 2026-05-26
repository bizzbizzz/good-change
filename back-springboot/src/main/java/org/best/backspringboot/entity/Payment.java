package org.best.backspringboot.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Long paymentId;
    private Long memberId;
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
    private String cardNumber;  // 없으면 추가
    private String transactionType;
    private Long merchantId;
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
    private String memberName;
    private String birthDate;
    private String organization;
    private String gender;
}