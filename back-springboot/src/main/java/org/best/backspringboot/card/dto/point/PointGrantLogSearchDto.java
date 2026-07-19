package org.best.backspringboot.card.dto.point;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.commonDTO.SearchBase;

@Getter
@Setter
public class PointGrantLogSearchDto extends SearchBase {
    private String grantId;     // 배치 UUID로 필터
    private String title;       // 지급명 검색
    private String memberName;  // 수혜자명 검색
    private String status;      // SUCCESS / FAIL
    private String startDate;   // 지급일 시작 (yyyy-MM-dd)
    private String endDate;     // 지급일 종료 (yyyy-MM-dd)
}
