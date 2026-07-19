package org.best.backspringboot.siteConfig.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.siteConfig.entity.SiteConfig;
import org.best.backspringboot.siteConfig.mapper.SiteConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SiteConfigService {

    private final SiteConfigMapper siteConfigMapper;

    @Transactional(readOnly = true)
    public List<SiteConfig> getAll(String configKey, String configVal) {
        return siteConfigMapper.findAll(configKey, configVal);
    }

    @Transactional
    public void insert(SiteConfig dto) {
        siteConfigMapper.insert(dto);
    }

    @Transactional
    public void update(String configKey, String configVal, Integer sortNo, String useYn) {
        siteConfigMapper.findByKey(configKey)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설정입니다."));
        siteConfigMapper.update(configKey, configVal, sortNo, useYn);
    }

    @Transactional
    public void delete(String configKey) {
        siteConfigMapper.findByKey(configKey)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설정입니다."));
        siteConfigMapper.delete(configKey);
    }

    @Transactional(readOnly = true)
    public Optional<SiteConfig> findByKey(String configKey) {
        return siteConfigMapper.findByKey(configKey);
    }
}