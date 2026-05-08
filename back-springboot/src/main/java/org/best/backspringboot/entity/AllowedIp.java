package org.best.backspringboot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AllowedIp {
    private Long ipId;
    private String ipAddress;
    private Long merchantId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}