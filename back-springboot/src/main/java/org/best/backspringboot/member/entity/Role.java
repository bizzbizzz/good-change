package org.best.backspringboot.member.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Role {
    private Long   roleId;
    private String roleName;
    private String description;
    private LocalDateTime createdAt;
}