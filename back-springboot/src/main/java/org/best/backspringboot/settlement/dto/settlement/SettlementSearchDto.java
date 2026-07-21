package org.best.backspringboot.settlement.dto.settlement;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.global.commonDTO.SearchBase;

@Getter
@Setter
public class SettlementSearchDto extends SearchBase {
    private String startMonth;    // 조회 시작월 (YYYY-MM)
    private String endMonth;      // 조회 종료월 (YYYY-MM)
    private String status;        // 정산상태 (PENDING/COMPLETED/FAILED)
    private String businessNumber; // 사업자번호
    private String merchantName;  // 가맹점명
    private Long merchantId;   // ✅ 추가
}