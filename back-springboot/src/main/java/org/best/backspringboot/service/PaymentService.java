package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.payment.PaymentCreateDto;
import org.best.backspringboot.dto.payment.PaymentResponseDto;
import org.best.backspringboot.dto.payment.PaymentSearchDto;
import org.best.backspringboot.entity.Card;
import org.best.backspringboot.entity.Member;
import org.best.backspringboot.entity.Payment;
import org.best.backspringboot.mapper.CardMapper;
import org.best.backspringboot.mapper.MemberMapper;
import org.best.backspringboot.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final CardMapper cardMapper;
    private final MemberMapper memberMapper;

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponseDto> getAll(PaymentSearchDto dto) {
        PageResponse<PaymentResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(dto.getPage());
        pageResponse.setSize(dto.getSize());

        List<PaymentResponseDto> content = paymentMapper.findAll(dto).stream()
                .map(PaymentResponseDto::from)
                .collect(Collectors.toList());

        long totalCount  = paymentMapper.countAll(dto);
        long totalAmount = paymentMapper.sumAmount(dto);

        pageResponse.setPageInfo(content, totalCount);
        pageResponse.setTotalAmount(totalAmount);
        pageResponse.setSuccessCount(totalCount);  // 전체 건수 사용

        return pageResponse;
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getById(Long paymentId, String transmissionDate) {
        return paymentMapper.findById(paymentId, transmissionDate)
                .map(PaymentResponseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제내역입니다."));
    }

    @Transactional
    public PaymentResponseDto pay(PaymentCreateDto dto) {

        // 1. 카드 조회
        Card card = cardMapper.findByCardNumber(dto.getCardNumber())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드번호입니다."));

        // 2. 카드 상태 체크
        if (!card.getStatus().equals("ACTIVE")) {
            throw new IllegalArgumentException("사용 불가능한 카드입니다.");
        }

        // 3. 회원 조회
        Member member = memberMapper.findById(card.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 4. 회원 상태 체크
        if (!member.getStatus().equals("ACTIVE")) {
            throw new IllegalArgumentException("사용 불가능한 회원입니다.");
        }

        // 5. 포인트 체크
        if (member.getPoint() < dto.getAmount()) {
            throw new IllegalArgumentException("보유 포인트가 부족합니다. 보유포인트: " + member.getPoint());
        }

        // 6. 승인번호 생성 (8자리 랜덤)
        String approvalNumber = String.format("%08d", (int)(Math.random() * 100000000));

        // 7. 전문번호 생성 (yyyyMMddHHmmssSSS)
        String messageNumber = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        // 8. payment INSERT
        Payment payment = Payment.builder()
                .merchantId(dto.getMerchantId())
                .memberId(card.getMemberId())
                .cardNumber(card.getCardNumber())
                .institutionCode("bizline")
                .messageNumber(messageNumber)
                .transmissionDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .amount(dto.getAmount())
                .transactionType("사용")
                .approvalNumber(approvalNumber)
                .inputMethod("WEB")
                .status("SUCCESS")
                .build();

        paymentMapper.insert(payment);

        // 9. 포인트 차감
        memberMapper.updatePoint(member.getMemberId(), member.getPoint() - dto.getAmount());

        // ✅ card, member 객체 함께 전달
        return PaymentResponseDto.from(payment, card, member);
    }

    @Transactional
    public void delete(Long paymentId, String transmissionDate) {
        paymentMapper.findById(paymentId, transmissionDate)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제내역입니다."));
        paymentMapper.updateStatusAndDate(paymentId, "DELETED", transmissionDate);
    }

    @Transactional
    public void cancel(Long paymentId, String transmissionDate) {
        // 1. 결제 조회
        Payment payment = paymentMapper.findById(paymentId, transmissionDate)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제내역입니다."));

        // 2. 결제 상태 체크
        if (!payment.getStatus().equals("SUCCESS")) {
            throw new IllegalArgumentException("취소 가능한 결제가 아닙니다.");
        }

        // 3. 15일 초과 체크
        if (payment.getCreatedAt() != null) {
            LocalDateTime fifteenDaysAgo = LocalDateTime.now().minusDays(15);
            if (payment.getCreatedAt().isBefore(fifteenDaysAgo)) {
                throw new IllegalArgumentException("결제일로부터 15일이 지난 결제는 취소할 수 없습니다.");
            }
        }

        // 4. member_id로 회원 직접 조회
        Member member = memberMapper.findById(payment.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 5. 결제 취소 처리
        paymentMapper.updateStatusAndDate(paymentId, "CANCELED",
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        // 6. 포인트 복원
        memberMapper.updatePoint(member.getMemberId(), member.getPoint() + payment.getAmount());
    }
}