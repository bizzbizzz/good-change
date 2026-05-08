package org.best.backspringboot.dto.allowedip;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AllowedIpCreateDto {

    @NotBlank
    @Size(max = 50)
    private String ipAddress;

    private Long merchantId;

    @Size(max = 100)
    private String description;
}