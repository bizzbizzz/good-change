package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.allowedip.AllowedIpCreateDto;
import org.best.backspringboot.entity.AllowedIp;
import org.best.backspringboot.mapper.AllowedIpMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AllowedIpService {

    private final AllowedIpMapper allowedIpMapper;

    @Transactional
    public void create(AllowedIpCreateDto dto) {
        // 중복 IP 체크
        if (allowedIpMapper.existsByIp(dto.getIpAddress())) {
            throw new IllegalArgumentException("이미 등록된 IP입니다.");
        }
        allowedIpMapper.insert(dto);
    }

    @Transactional(readOnly = true)
    public List<AllowedIp> getAll() {
        return allowedIpMapper.findAll();
    }

    @Transactional
    public void delete(Long ipId) {
        allowedIpMapper.deleteById(ipId);
    }
}