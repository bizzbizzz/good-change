package org.best.backspringboot.siteConfig.dto.banner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BannerCreateDto {

    @NotBlank(message = "배너 제목은 필수입니다.")
    private String title;

    @NotNull(message = "이미지 파일은 필수입니다.")
    private MultipartFile imageFile;

    private String  linkUrl;            // 클릭 시 이동 URL (선택)
    private Integer sortNo = 1;         // 노출 순서 (기본 1)
}
