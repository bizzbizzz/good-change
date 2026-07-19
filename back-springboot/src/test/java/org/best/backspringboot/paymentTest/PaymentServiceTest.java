package org.best.backspringboot.paymentTest;

import org.best.backspringboot.payment.dto.payment.PaymentCreateDto;
import org.best.backspringboot.payment.dto.payment.PaymentResponseDto;
import org.best.backspringboot.payment.dto.payment.PaymentSearchDto;
import org.best.backspringboot.card.entity.Card;
import org.best.backspringboot.member.entity.Member;
import org.best.backspringboot.payment.entity.Payment;
import org.best.backspringboot.card.mapper.CardMapper;
import org.best.backspringboot.member.mapper.MemberMapper;
import org.best.backspringboot.payment.mapper.PaymentMapper;
import org.best.backspringboot.payment.service.PaymentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 테스트")
class PaymentServiceTest {

    @Mock PaymentMapper paymentMapper;
    @Mock CardMapper cardMapper;
    @Mock MemberMapper memberMapper;
    @InjectMocks PaymentService paymentService;

    private Card mockCard() throws Exception {
        Card c = new Card();
        setField(c, "cardId",     1L);
        setField(c, "memberId",   1L);
        setField(c, "cardNumber", "1234567890123456");
        setField(c, "status",     "ACTIVE");
        return c;
    }

    private Member mockMember(long point) throws Exception {
        Member m = new Member();
        setField(m, "memberId", 1L);
        setField(m, "status",   "ACTIVE");
        setField(m, "point",    point);
        return m;
    }

    private Payment mockPayment(String status, LocalDateTime createdAt) {
        return Payment.builder()  // ✅ builder 사용
                .paymentId(1L)
                .cardId(1L)
                .amount(50000L)
                .status(status)
                .createdAt(createdAt)
                .build();
    }

    private void setField(Object obj, String name, Object val) throws Exception {
        var field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, val);
    }

    // ── 결제 ──────────────────────────────────────────
    @Test
    @DisplayName("결제 성공")
    void pay_success() throws Exception {
        PaymentCreateDto dto = mock(PaymentCreateDto.class);
        given(dto.getCardNumber()).willReturn("1234567890123456");
        given(dto.getAmount()).willReturn(50000L);
        given(cardMapper.findByCardNumber("1234567890123456")).willReturn(Optional.of(mockCard()));
        given(memberMapper.findById(1L)).willReturn(Optional.of(mockMember(100000L)));

        PaymentResponseDto result = paymentService.pay(dto);
        assertThat(result).isNotNull();
        then(memberMapper).should().updatePoint(eq(1L), eq(50000L));
    }

    @Test
    @DisplayName("결제 실패 - 카드 없음")
    void pay_cardNotFound() {
        PaymentCreateDto dto = mock(PaymentCreateDto.class);
        given(dto.getCardNumber()).willReturn("9999999999999999");
        given(cardMapper.findByCardNumber(anyString())).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.pay(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 카드번호");
    }

    @Test
    @DisplayName("결제 실패 - 카드 비활성")
    void pay_cardInactive() throws Exception {
        Card blockedCard = mockCard();
        setField(blockedCard, "status", "BLOCKED");
        PaymentCreateDto dto = mock(PaymentCreateDto.class);
        given(dto.getCardNumber()).willReturn("1234567890123456");
        given(cardMapper.findByCardNumber(anyString())).willReturn(Optional.of(blockedCard));

        assertThatThrownBy(() -> paymentService.pay(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 불가능한 카드");
    }

    @Test
    @DisplayName("결제 실패 - 포인트 부족")
    void pay_insufficientPoint() throws Exception {
        PaymentCreateDto dto = mock(PaymentCreateDto.class);
        given(dto.getCardNumber()).willReturn("1234567890123456");
        given(dto.getAmount()).willReturn(999999999L);
        given(cardMapper.findByCardNumber(anyString())).willReturn(Optional.of(mockCard()));
        given(memberMapper.findById(1L)).willReturn(Optional.of(mockMember(1000L)));

        assertThatThrownBy(() -> paymentService.pay(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("보유 포인트가 부족");
    }

    // ── 결제 취소 ──────────────────────────────────────
    @Test
    @DisplayName("결제 취소 성공")
    void cancel_success() throws Exception {
        given(paymentMapper.findById(1L)).willReturn(Optional.of(mockPayment("SUCCESS", LocalDateTime.now())));
        given(cardMapper.findById(1L)).willReturn(Optional.of(mockCard()));
        given(memberMapper.findById(1L)).willReturn(Optional.of(mockMember(50000L)));

        assertThatNoException().isThrownBy(() -> paymentService.cancel(1L));
        then(paymentMapper).should().updateStatus(1L, "CANCELED");
        then(memberMapper).should().updatePoint(eq(1L), eq(100000L)); // 포인트 복원
    }

    @Test
    @DisplayName("결제 취소 실패 - 이미 취소된 결제")
    void cancel_alreadyCanceled() throws Exception {
        given(paymentMapper.findById(1L)).willReturn(Optional.of(mockPayment("CANCELED", LocalDateTime.now())));

        assertThatThrownBy(() -> paymentService.cancel(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("취소 가능한 결제가 아닙니다");
    }

    @Test
    @DisplayName("결제 취소 실패 - 15일 초과")
    void cancel_expired() throws Exception {
        LocalDateTime oldDate = LocalDateTime.now().minusDays(16);
        given(paymentMapper.findById(1L)).willReturn(Optional.of(mockPayment("SUCCESS", oldDate)));

        assertThatThrownBy(() -> paymentService.cancel(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("15일");
    }

    // ── 조회 ──────────────────────────────────────────
    @Test
    @DisplayName("결제 단건 조회 성공")
    void getById_success() throws Exception {
        given(paymentMapper.findById(1L)).willReturn(Optional.of(mockPayment("SUCCESS", LocalDateTime.now())));
        PaymentResponseDto result = paymentService.getById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("결제 단건 조회 실패")
    void getById_notFound() {
        given(paymentMapper.findById(999L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> paymentService.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 결제내역");
    }

    @Test
    @DisplayName("결제 내역 전체 조회")
    void getAll_success() throws Exception {
        PaymentSearchDto dto = new PaymentSearchDto();
        given(paymentMapper.findAll(dto)).willReturn(List.of(mockPayment("SUCCESS", LocalDateTime.now())));
        given(paymentMapper.countAll(dto)).willReturn(1L);

        var result = paymentService.getAll(dto);
        assertThat(result.getContent()).hasSize(1);
    }

    // ── 삭제 ──────────────────────────────────────────
    @Test
    @DisplayName("결제 내역 삭제 성공 (DELETED 처리)")
    void delete_success() throws Exception {
        given(paymentMapper.findById(1L)).willReturn(Optional.of(mockPayment("SUCCESS", LocalDateTime.now())));
        assertThatNoException().isThrownBy(() -> paymentService.delete(1L));
        then(paymentMapper).should().updateStatus(1L, "DELETED");
    }
}
