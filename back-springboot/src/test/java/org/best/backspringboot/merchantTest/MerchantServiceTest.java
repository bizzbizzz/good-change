package org.best.backspringboot.merchantTest;

import org.best.backspringboot.merchant.dto.merchant.MerchantCreateDto;
import org.best.backspringboot.merchant.dto.merchant.MerchantResponseDto;
import org.best.backspringboot.merchant.dto.merchant.MerchantUpdateDto;
import org.best.backspringboot.merchant.entity.Merchant;
import org.best.backspringboot.merchant.mapper.MerchantMapper;
import org.best.backspringboot.merchant.service.MerchantService;
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
@DisplayName("MerchantService 테스트")
class MerchantServiceTest {

    @Mock MerchantMapper merchantMapper;
    @InjectMocks MerchantService merchantService;

    private Merchant mockMerchant() throws Exception {
        Merchant m = new Merchant();
        setField(m, "merchantId",     1L);
        setField(m, "memberId",       1L);
        setField(m, "merchantName",   "테스트가맹점");
        setField(m, "businessNumber", "1234567890");
        setField(m, "terminalId",     "TERM000001");
        setField(m, "status",         "ACTIVE");
        return m;
    }

    private void setField(Object obj, String name, Object val) throws Exception {
        var field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, val);
    }

    @Test
    @DisplayName("가맹점 등록 성공")
    void create_success() {
        given(merchantMapper.findByBusinessNumber(anyString())).willReturn(Optional.empty());
        MerchantCreateDto dto = new MerchantCreateDto();
        dto.setBusinessNumber("1234567890");
        assertThatNoException().isThrownBy(() -> merchantService.create(dto));
        then(merchantMapper).should().insert(dto);
    }

    @Test
    @DisplayName("가맹점 등록 실패 - 사업자번호 중복")
    void create_duplicateBusinessNumber() {
        given(merchantMapper.findByBusinessNumber(anyString())).willReturn(Optional.of(new Merchant()));
        MerchantCreateDto dto = new MerchantCreateDto();
        dto.setBusinessNumber("1234567890");
        assertThatThrownBy(() -> merchantService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 등록된 사업자번호");
    }

    @Test
    @DisplayName("가맹점 단건 조회 성공")
    void getById_success() throws Exception {
        given(merchantMapper.findById(1L)).willReturn(Optional.of(mockMerchant()));
        given(merchantMapper.findCategoriesByMerchantId(1L)).willReturn(List.of());
        MerchantResponseDto result = merchantService.getById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("가맹점 단건 조회 실패")
    void getById_notFound() {
        given(merchantMapper.findById(999L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> merchantService.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 가맹점");
    }

    @Test
    @DisplayName("가맹점 수정 성공")
    void update_success() throws Exception {
        given(merchantMapper.findById(1L)).willReturn(Optional.of(mockMerchant()));
        MerchantUpdateDto dto = new MerchantUpdateDto();
        assertThatNoException().isThrownBy(() -> merchantService.update(1L, dto));
        then(merchantMapper).should().update(1L, dto);
    }

    @Test
    @DisplayName("가맹점 삭제 성공")
    void delete_success() throws Exception {
        given(merchantMapper.findById(1L)).willReturn(Optional.of(mockMerchant()));
        assertThatNoException().isThrownBy(() -> merchantService.delete(1L));
        then(merchantMapper).should().delete(1L);
    }
}
