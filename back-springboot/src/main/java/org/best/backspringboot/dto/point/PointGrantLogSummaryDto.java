package org.best.backspringboot.dto.pointgrant;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PointGrantLogSummaryDto {
    private String        grantId;
    private String        title;
    private Long          grantAmount;
    private int           totalCount;
    private int           successCount;
    private int           failCount;
    private Long          grantedBy;
    private LocalDateTime grantedAt;
}
