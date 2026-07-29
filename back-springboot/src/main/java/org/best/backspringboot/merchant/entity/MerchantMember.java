package org.best.backspringboot.merchant.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MerchantMember {
    private Long   id;
    private Long   merchantId;
    private Long   memberId;
    private Long roleId;
    private String roleName;
    private LocalDateTime createdAt;
}