package org.best.backspringboot.site.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Banner {
    private Long    bannerId;
    private String  title;
    private String  imageUrl;
    private String  linkUrl;
    private Integer sortNo;
    private String  useYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
