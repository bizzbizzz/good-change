package org.best.backspringboot.payment.entity;

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
    private Long merchantId;
    private String kocesCd;          // 추가
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
    private String cardNumber;
    private String trackData;        // track → trackData
    private String keyIn;            // 추가
    private String inputMethod;
    private Long amount;
    private String transactionType;
    private String approvalNumber;
    private String responseCode;
    private String cancelCode;
    private String originalTradeDate;
    private String originalApprovalNumber;
    private Long originalAmount;
    private Long remainingPoint;
    private String filterValue;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String memberName;
    private String category;        // 업종명 (merchant_category.category_name)
    private String birthDate;
    private String organization;
    private String gender;
}
