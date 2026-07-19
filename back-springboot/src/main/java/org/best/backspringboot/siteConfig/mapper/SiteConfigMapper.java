package org.best.backspringboot.siteConfig.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.siteConfig.entity.SiteConfig;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SiteConfigMapper {
    List<SiteConfig> findAll(@Param("configKey") String configKey, @Param("configVal") String configVal);
    Optional<SiteConfig> findByKey(String configKey);
    void insert(SiteConfig siteConfig);
    void update(@Param("configKey") String configKey, @Param("configVal") String configVal,
                @Param("sortNo") Integer sortNo, @Param("useYn") String useYn);
    void delete(String configKey);
}
