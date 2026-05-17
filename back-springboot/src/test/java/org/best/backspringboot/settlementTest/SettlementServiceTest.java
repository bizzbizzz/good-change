package org.best.backspringboot.settlementTest;

import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.settlement.SettlementResponseDto;
import org.best.backspringboot.dto.settlement.SettlementSearchDto;
import org.best.backspringboot.entity.Settlement;
import org.best.backspringboot.mapper.SettlementMapper;
import org.best.backspringboot.service.SettlementService;
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
@DisplayName("SettlementService 테스트")
class SettlementServiceTest {

    @Mock SettlementMapper settlementMapper;
    @InjectMocks SettlementService settlementService;

    private Settlement mockSettlement(String status) throws Exception {
        Settlement s = new Settlement();
        setField(s, "settlementId",     1L);
        setField(s, "merchantId",       1L);
        setField(s, "merchantName",     "테스트가맹점");
        setField(s, "businessNumber",   "1234567890");
        setField(s, "settlementMonth",  "2026-05");
        setField(s, "settlementAmount", 500000L);
        setField(s, "status",           status);
        return s;
    }

    private void setField(Object obj, String name, Object val) throws Exception {
        var field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, val);
    }

    // ── READ ──────────────────────────────────────────
    @Test
    @DisplayName("정산 단건 조회 성공")
    void getById_success() throws Exception {
        given(settlementMapper.findById(1L)).willReturn(Optional.of(mockSettlement("PENDING")));
        SettlementResponseDto result = settlementService.getById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getSettlementId()).isEqualTo(1L);
        assertThat(result.getMerchantName()).isEqualTo("테스트가맹점");
        assertThat(result.getSettlementAmount()).isEqualTo(500000L);
        assertThat(result.getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("정산 단건 조회 실패 - 없는 정산")
    void getById_notFound() {
        given(settlementMapper.findById(999L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> settlementService.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 정산내역");
    }

    @Test
    @DisplayName("정산 전체 조회 (페이징)")
    void getAll_success() throws Exception {
        SettlementSearchDto dto = new SettlementSearchDto();
        given(settlementMapper.findAll(dto)).willReturn(List.of(mockSettlement("PENDING")));
        given(settlementMapper.countAll(dto)).willReturn(1L);

        PageResponse<SettlementResponseDto> result = settlementService.getAll(dto);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("정산 전체 조회 - 빈 결과")
    void getAll_empty() {
        SettlementSearchDto dto = new SettlementSearchDto();
        given(settlementMapper.findAll(dto)).willReturn(List.of());
        given(settlementMapper.countAll(dto)).willReturn(0L);

        PageResponse<SettlementResponseDto> result = settlementService.getAll(dto);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalCount()).isEqualTo(0L);
    }

    // ── STATUS UPDATE ─────────────────────────────────
    @Test
    @DisplayName("정산 상태 변경 성공 - PENDING → COMPLETED")
    void updateStatus_toCompleted() throws Exception {
        given(settlementMapper.findById(1L)).willReturn(Optional.of(mockSettlement("PENDING")));
        assertThatNoException().isThrownBy(() -> settlementService.updateStatus(1L, "COMPLETED"));
        then(settlementMapper).should().updateStatus(1L, "COMPLETED");
    }

    @Test
    @DisplayName("정산 상태 변경 성공 - PENDING → FAILED")
    void updateStatus_toFailed() throws Exception {
        given(settlementMapper.findById(1L)).willReturn(Optional.of(mockSettlement("PENDING")));
        assertThatNoException().isThrownBy(() -> settlementService.updateStatus(1L, "FAILED"));
        then(settlementMapper).should().updateStatus(1L, "FAILED");
    }

    @Test
    @DisplayName("정산 상태 변경 성공 - COMPLETED → DELETED")
    void updateStatus_toDeleted() throws Exception {
        given(settlementMapper.findById(1L)).willReturn(Optional.of(mockSettlement("COMPLETED")));
        assertThatNoException().isThrownBy(() -> settlementService.updateStatus(1L, "DELETED"));
        then(settlementMapper).should().updateStatus(1L, "DELETED");
    }

    @Test
    @DisplayName("정산 상태 변경 실패 - 없는 정산")
    void updateStatus_notFound() {
        given(settlementMapper.findById(999L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> settlementService.updateStatus(999L, "COMPLETED"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 정산내역");
    }
}
