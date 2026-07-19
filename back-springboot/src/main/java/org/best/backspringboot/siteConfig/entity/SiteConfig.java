package org.best.backspringboot.siteConfig.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfig {
    private Long configNo;
    private String configKey;
    private String configVal;
    private Integer sortNo;
    private String useYn;
    private LocalDateTime regDt;
    private LocalDateTime modDt;
}
