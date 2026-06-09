package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.mapper.CardListMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardListService {

    private final CardListMapper cardListMapper;

    @Transactional
    public Map<String, Object> bulkInsert(List<String> cardNumbers) {
        int requested = (cardNumbers == null) ? 0 : cardNumbers.size();

        // 정제: trim + 16자리 숫자만 + 중복 제거
        List<String> valid = (cardNumbers == null) ? List.of()
                : cardNumbers.stream()
                    .filter(n -> n != null)
                    .map(String::trim)
                    .filter(n -> n.matches("\\d{16}"))   // 16자리 숫자만
                    .distinct()
                    .toList();

        // INSERT IGNORE → 이미 있는 번호는 건너뜀, 실제 들어간 개수 반환
        int inserted = valid.isEmpty() ? 0 : cardListMapper.bulkInsert(valid);

        Map<String, Object> result = new HashMap<>();
        result.put("requested", requested);          // 요청 개수
        result.put("validFormat", valid.size());     // 형식 통과 개수
        result.put("inserted", inserted);            // 실제 신규 등록 개수
        result.put("skipped", requested - inserted);  // 건너뜀(중복+형식오류)
        return result;
    }

    @Transactional(readOnly = true)
    public boolean isInCardList(String cardNumber) {
        // 16자리 형식 체크
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            return false;
        }
        return cardListMapper.existsByCardNumber(cardNumber);
    }
}