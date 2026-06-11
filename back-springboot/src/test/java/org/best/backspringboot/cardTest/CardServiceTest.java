package org.best.backspringboot;

import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.card.CardResponseDto;
import org.best.backspringboot.dto.card.CardSearchDto;
import org.best.backspringboot.entity.Card;
import org.best.backspringboot.entity.Member;
import org.best.backspringboot.mapper.CardMapper;
import org.best.backspringboot.mapper.MemberMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardService 테스트")
class CardServiceTest {

    @Mock CardMapper cardMapper;
    @Mock MemberMapper memberMapper;
    @InjectMocks CardService cardService;

    private Card mockCard() throws Exception {
        Card c = new Card();
        setField(c, "cardId",     1L);
        setField(c, "memberId",   1L);
        setField(c, "cardNumber", "1234567890123456");
        setField(c, "status",     "ACTIVE");
        return c;
    }

    private Member mockMember() throws Exception {
        Member m = new Member();
        setField(m, "memberId", 1L);
        setField(m, "name",     "홍길동");
        setField(m, "status",   "ACTIVE");
        return m;
    }

    private void setField(Object obj, String name, Object val) throws Exception {
        var field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, val);
    }

    // ── CREATE ────────────────────────────────────────
    @Test
    @DisplayName("카드 등록 성공")
    void create_success() {
        given(cardMapper.findByCardNumber("1234567890123456")).willReturn(Optional.empty());
        given(cardMapper.countByMemberId(1L)).willReturn(0L);

        CardCreateDto dto = new CardCreateDto();
        dto.setCardNumber("1234567890123456");
        dto.setMemberId(1L);

        assertThatNoException().isThrownBy(() -> cardService.create(dto));
        then(cardMapper).should().insert(dto);
    }

    @Test
    @DisplayName("카드 등록 실패 - 카드번호 중복")
    void create_duplicateCard() throws Exception {
        given(cardMapper.findByCardNumber("1234567890123456")).willReturn(Optional.of(mockCard()));

        CardCreateDto dto = new CardCreateDto();
        dto.setCardNumber("1234567890123456");

        assertThatThrownBy(() -> cardService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 등록된 카드번호");
    }

    @Test
    @DisplayName("카드 등록 실패 - 최대 3장 초과")
    void create_maxCardExceeded() {
        given(cardMapper.findByCardNumber(anyString())).willReturn(Optional.empty());
        given(cardMapper.countByMemberId(1L)).willReturn(3L);

        CardCreateDto dto = new CardCreateDto();
        dto.setCardNumber("9999999999999999");
        dto.setMemberId(1L);

        assertThatThrownBy(() -> cardService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최대 3장");
    }

    @Test
    @DisplayName("카드 등록 - 첫 번째 카드는 고유카드(isPrimary=1) 자동 설정")
    void create_firstCardSetAsPrimary() {
        given(cardMapper.findByCardNumber(anyString())).willReturn(Optional.empty());
        given(cardMapper.countByMemberId(1L)).willReturn(0L);

        CardCreateDto dto = new CardCreateDto();
        dto.setCardNumber("1234567890123456");
        dto.setMemberId(1L);
        // isPrimary 미설정

        cardService.create(dto);
        assertThat(dto.getIsPrimary()).isEqualTo(1);
    }

    @Test
    @DisplayName("카드 등록 - 두 번째 카드는 추가카드(isPrimary=0) 자동 설정")
    void create_secondCardSetAsAdditional() {
        given(cardMapper.findByCardNumber(anyString())).willReturn(Optional.empty());
        given(cardMapper.countByMemberId(1L)).willReturn(1L);

        CardCreateDto dto = new CardCreateDto();
        dto.setCardNumber("9999888877776666");
        dto.setMemberId(1L);

        cardService.create(dto);
        assertThat(dto.getIsPrimary()).isEqualTo(0);
    }

    // ── READ ──────────────────────────────────────────
    @Test
    @DisplayName("카드번호로 조회 성공")
    void getByCardNumber_success() throws Exception {
        given(cardMapper.findByCardNumber("1234567890123456")).willReturn(Optional.of(mockCard()));
        given(memberMapper.findById(1L)).willReturn(Optional.of(mockMember()));

        CardResponseDto result = cardService.getByCardNumber("1234567890123456");
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("카드번호로 조회 실패 - 없는 카드")
    void getByCardNumber_notFound() {
        given(cardMapper.findByCardNumber("9999999999999999")).willReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.getByCardNumber("9999999999999999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 카드번호");
    }

    @Test
    @DisplayName("회원별 카드 조회")
    void getByMemberId_success() throws Exception {
        given(cardMapper.findByMemberId(1L)).willReturn(List.of(mockCard()));
        given(memberMapper.findById(1L)).willReturn(Optional.of(mockMember()));

        List<CardResponseDto> result = cardService.getByMemberId(1L);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("회원별 카드 조회 - 카드 없음")
    void getByMemberId_empty() {
        given(cardMapper.findByMemberId(1L)).willReturn(List.of());

        List<CardResponseDto> result = cardService.getByMemberId(1L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("카드 전체 조회 (페이징)")
    void getAll_success() throws Exception {
        CardSearchDto dto = new CardSearchDto();
        given(cardMapper.findAll(dto)).willReturn(List.of(mockCard()));
        given(cardMapper.countAll(dto)).willReturn(1L);
        given(memberMapper.findById(1L)).willReturn(Optional.of(mockMember()));

        PageResponse<CardResponseDto> result = cardService.getAll(dto);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalCount()).isEqualTo(1L);
    }

    // ── DELETE ────────────────────────────────────────
    @Test
    @DisplayName("카드 삭제 성공")
    void delete_success() throws Exception {
        given(cardMapper.findById(1L)).willReturn(Optional.of(mockCard()));
        assertThatNoException().isThrownBy(() -> cardService.delete(1L));
        then(cardMapper).should().delete(1L);
    }

    @Test
    @DisplayName("카드 삭제 실패 - 없는 카드")
    void delete_notFound() {
        given(cardMapper.findById(999L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> cardService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 카드");
    }
}
