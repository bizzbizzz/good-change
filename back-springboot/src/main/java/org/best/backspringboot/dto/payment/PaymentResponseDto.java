package org.best.backspringboot.dto.payment;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.entity.Card;
import org.best.backspringboot.entity.Member;
import org.best.backspringboot.entity.Payment;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponseDto {
    private Long paymentId;
    private Long cardId;
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
    private Long amount;
    private String transactionType;
    private String approvalNumber;
    private String responseCode;
    private String cancelCode;
    private String originalTradeDate;
    private String originalApprovalNumber;
    private String acquirerCode;
    private String acquirerName;
    private String status;
    private LocalDateTime createdAt;
    private String cardNumber;      // ✅ 추가
    private Long originalAmount;    // ✅ 추가
    private Long remainingPoint;    // ✅ 추가

    // 조회용 (card, member 없이)
    public static PaymentResponseDto from(Payment payment) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .cardId(payment.getCardId())
                .messageNumber(payment.getMessageNumber())
                .institutionCode(payment.getInstitutionCode())
                .transmissionDate(payment.getTransmissionDate())
                .traceNumber(payment.getTraceNumber())
                .terminalId(payment.getTerminalId())
                .businessNumber(payment.getBusinessNumber())
                .merchantNumber(payment.getMerchantNumber())
                .merchantName(payment.getMerchantName())
                .representative(payment.getRepresentative())
                .phone(payment.getPhone())
                .address(payment.getAddress())
                .cardType(payment.getCardType())
                .inputMethod(payment.getInputMethod())
                .amount(payment.getAmount())
                .transactionType(payment.getTransactionType())
                .approvalNumber(payment.getApprovalNumber())
                .responseCode(payment.getResponseCode())
                .cancelCode(payment.getCancelCode())
                .originalTradeDate(payment.getOriginalTradeDate())
                .originalApprovalNumber(payment.getOriginalApprovalNumber())
                .acquirerCode(payment.getAcquirerCode())
                .acquirerName(payment.getAcquirerName())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    // 결제용 (card, member 포함)
    public static PaymentResponseDto from(Payment payment, Card card, Member member) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .cardId(payment.getCardId())
                .messageNumber(payment.getMessageNumber())
                .institutionCode(payment.getInstitutionCode())
                .transmissionDate(payment.getTransmissionDate())
                .traceNumber(payment.getTraceNumber())
                .terminalId(payment.getTerminalId())
                .businessNumber(payment.getBusinessNumber())
                .merchantNumber(payment.getMerchantNumber())
                .merchantName(payment.getMerchantName())
                .representative(payment.getRepresentative())
                .phone(payment.getPhone())
                .address(payment.getAddress())
                .cardNumber(card != null ? card.getCardNumber() : null)
                .cardType(payment.getCardType())
                .inputMethod(payment.getInputMethod())
                .amount(payment.getAmount())
                .originalAmount(member != null ? member.getPoint() : null)
                .remainingPoint(member != null ? member.getPoint() - payment.getAmount() : null)
                .transactionType(payment.getTransactionType())
                .approvalNumber(payment.getApprovalNumber())
                .responseCode(payment.getResponseCode())
                .cancelCode(payment.getCancelCode())
                .originalTradeDate(payment.getOriginalTradeDate())
                .originalApprovalNumber(payment.getOriginalApprovalNumber())
                .acquirerCode(payment.getAcquirerCode())
                .acquirerName(payment.getAcquirerName())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

}