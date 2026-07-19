package org.best.backspringboot.siteConfig.dto.banner;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.commonDTO.SearchBase;

@Getter
@Setter
public class BannerSearchDto extends SearchBase {
    private String title;   // 제목 검색
    private String useYn;   // Y / N / null(전체)
}
