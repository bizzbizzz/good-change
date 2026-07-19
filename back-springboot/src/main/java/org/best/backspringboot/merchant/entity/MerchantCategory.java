package org.best.backspringboot.merchant.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCategory {
    private Long          categoryId;
    private String        categoryName;
    private String        description;
    private LocalDateTime createdAt;
}
