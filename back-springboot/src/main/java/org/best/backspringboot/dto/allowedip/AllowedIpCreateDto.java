package org.best.backspringboot.dto.allowedip;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor  // 추가
@AllArgsConstructor // 추가
public class AllowedIpCreateDto {

    private Long ipId;  // 이것도 추가

    @NotBlank
    @Size(max = 50)
    private String ipAddress;

    private Long merchantId;

    @Size(max = 100)
    private String description;
}