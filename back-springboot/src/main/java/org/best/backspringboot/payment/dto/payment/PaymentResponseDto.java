package org.best.backspringboot.payment.dto.payment;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.card.entity.Card;
import org.best.backspringboot.member.entity.Member;
import org.best.backspringboot.payment.entity.Payment;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponseDto {
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
    private String trackData;        // 추가
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
    private String status;
    private LocalDateTime createdAt;
    private String memberName;
    private String birthDate;
    private String organization;
    private String gender;
    private String category;        // 업종명 (merchant_category.category_name)

    // 조회용 (card, member 없이)
    public static PaymentResponseDto from(Payment payment) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .memberId(payment.getMemberId())
                .merchantId(payment.getMerchantId())
                .kocesCd(payment.getKocesCd())
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
                .cardNumber(payment.getCardNumber())
                .trackData(payment.getTrackData())
                .keyIn(payment.getKeyIn())
                .inputMethod(payment.getInputMethod())
                .amount(payment.getAmount())
                .transactionType(payment.getTransactionType())
                .approvalNumber(payment.getApprovalNumber())
                .responseCode(payment.getResponseCode())
                .cancelCode(payment.getCancelCode())
                .originalTradeDate(payment.getOriginalTradeDate())
                .originalApprovalNumber(payment.getOriginalApprovalNumber())
                .originalAmount(payment.getOriginalAmount())
                .remainingPoint(payment.getRemainingPoint())
                .memberName(payment.getMemberName())
                .birthDate(payment.getBirthDate())
                .organization(payment.getOrganization())
                .gender(payment.getGender())
                .category(payment.getCategory())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    // 결제용 (card, member 포함)
    public static PaymentResponseDto from(Payment payment, Card card, Member member) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .memberId(payment.getMemberId())
                .merchantId(payment.getMerchantId())
                .kocesCd(payment.getKocesCd())
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
                .cardNumber(card != null ? card.getCardNumber() : payment.getCardNumber())
                .trackData(payment.getTrackData())
                .keyIn(payment.getKeyIn())
                .inputMethod(payment.getInputMethod())
                .amount(payment.getAmount())
                .originalAmount(payment.getOriginalAmount())
                .remainingPoint(payment.getRemainingPoint())
                .transactionType(payment.getTransactionType())
                .approvalNumber(payment.getApprovalNumber())
                .responseCode(payment.getResponseCode())
                .cancelCode(payment.getCancelCode())
                .originalTradeDate(payment.getOriginalTradeDate())
                .originalApprovalNumber(payment.getOriginalApprovalNumber())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}