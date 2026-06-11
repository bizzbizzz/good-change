package org.best.backspringboot.dto.pointgrant;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.entity.PointGrantLog;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointGrantLogResponseDto {

    private Long          logId;
    private String        grantId;
    private String        title;
    private Long          grantAmount;
    private Long          memberId;
    private String        memberName;
    private Long          beforePoint;
    private Long          afterPoint;
    private String        status;
    private String        failReason;
    private Long          grantedBy;
    private LocalDateTime createdAt;

    public static PointGrantLogResponseDto from(PointGrantLog log) {
        return PointGrantLogResponseDto.builder()
                .logId(log.getLogId())
                .grantId(log.getGrantId())
                .title(log.getTitle())
                .grantAmount(log.getGrantAmount())
                .memberId(log.getMemberId())
                .memberName(log.getMemberName())
                .beforePoint(log.getBeforePoint())
                .afterPoint(log.getAfterPoint())
                .status(log.getStatus())
                .failReason(log.getFailReason())
                .grantedBy(log.getGrantedBy())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
