package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.mapper.CardListMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            return false;
        }
        return cardListMapper.existsByCardNumber(cardNumber);
    }

    @Transactional
    public Map<String, Object> generateAndInsert(int count) {
        if (count <= 0 || count > 1000) {
            throw new IllegalArgumentException("생성 개수는 1~1000 사이여야 합니다.");
        }

        List<String> generated = new ArrayList<>();
        int maxAttempts = count * 10; // 중복 회피를 위한 최대 시도 횟수
        int attempts = 0;

        while (generated.size() < count && attempts < maxAttempts) {
            attempts++;
            String cardNumber = generateRandomCardNumber();
            // card_list에 없는 번호만 추가
            if (!cardListMapper.existsByCardNumber(cardNumber)
                    && !generated.contains(cardNumber)) {
                generated.add(cardNumber);
            }
        }

        int inserted = generated.isEmpty() ? 0 : cardListMapper.bulkInsert(generated);

        Map<String, Object> result = new HashMap<>();
        result.put("requested", count);
        result.put("generated", generated.size());
        result.put("inserted", inserted);
        result.put("cardNumbers", generated); // 생성된 번호 목록 반환
        return result;
    }

    // 9876로 시작하는 16자리 랜덤 카드번호 생성
    private String generateRandomCardNumber() {
        String prefix = "9876";
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 12; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
}