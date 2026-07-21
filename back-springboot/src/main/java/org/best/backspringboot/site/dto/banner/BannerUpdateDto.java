package org.best.backspringboot.site.dto.banner;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BannerUpdateDto {

    private String       title;
    private MultipartFile imageFile;  // null이면 기존 이미지 유지
    private String       linkUrl;
    private Integer      sortNo;
    private String       useYn;       // Y / N
}
