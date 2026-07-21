package org.best.backspringboot.site.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.site.dto.banner.BannerSearchDto;
import org.best.backspringboot.site.entity.Banner;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BannerMapper {
    List<Banner> findAll(BannerSearchDto searchDto);
    long countAll(BannerSearchDto searchDto);
    Optional<Banner> findById(Long bannerId);
    void insert(Banner banner);
    void update(@Param("bannerId") Long bannerId,
                @Param("title")    String title,
                @Param("linkUrl")  String linkUrl,
                @Param("sortNo")   Integer sortNo,
                @Param("useYn")    String useYn,
                @Param("imageUrl") String imageUrl);
    void delete(Long bannerId);
}
