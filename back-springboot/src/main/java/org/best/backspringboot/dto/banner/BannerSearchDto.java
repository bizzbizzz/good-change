package org.best.backspringboot.dto.banner;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.dto.SearchBase;

@Getter
@Setter
public class BannerSearchDto extends SearchBase {
    private String title;   // 제목 검색
    private String useYn;   // Y / N / null(전체)
}
