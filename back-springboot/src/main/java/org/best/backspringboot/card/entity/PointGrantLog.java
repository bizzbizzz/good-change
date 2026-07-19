package org.best.backspringboot.card.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointGrantLog {
    private Long          logId;
    private String        grantId;       // UUID - 같은 일괄지급 묶음
    private String        title;         // 지급명
    private Long          grantAmount;   // 지급 포인트
    private Long          memberId;
    private String        memberName;    // 스냅샷
    private Long          beforePoint;
    private Long          afterPoint;
    private String        status;        // SUCCESS / FAIL
    private String        failReason;
    private Long          grantedBy;     // 관리자 member_id
    private LocalDateTime createdAt;
}
