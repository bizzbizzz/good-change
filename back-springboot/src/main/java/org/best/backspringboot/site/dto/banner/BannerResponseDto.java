package org.best.backspringboot.site.dto.banner;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.site.entity.Banner;

import java.time.LocalDateTime;

@Getter
@Builder
public class BannerResponseDto {

    private Long          bannerId;
    private String        title;
    private String        imageUrl;
    private String        linkUrl;
    private Integer       sortNo;
    private String        useYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BannerResponseDto from(Banner banner) {
        return BannerResponseDto.builder()
                .bannerId(banner.getBannerId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .linkUrl(banner.getLinkUrl())
                .sortNo(banner.getSortNo())
                .useYn(banner.getUseYn())
                .createdAt(banner.getCreatedAt())
                .updatedAt(banner.getUpdatedAt())
                .build();
    }
}
