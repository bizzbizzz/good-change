package org.best.backspringboot.merchant.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.merchant.dto.allowedip.AllowedIpCreateDto;
import org.best.backspringboot.merchant.entity.AllowedIp;
import org.best.backspringboot.merchant.mapper.AllowedIpMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

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
    public Optional<AllowedIp> getByMerchantId(Long merchantId) {
        return allowedIpMapper.findByMerchantId(merchantId);
    }

    @Transactional
    public void updateByMerchantId(Long merchantId, String ipAddress) {
        if (allowedIpMapper.findByMerchantId(merchantId).isPresent()) {
            allowedIpMapper.updateByMerchantId(merchantId, ipAddress);
        } else {
            allowedIpMapper.insert(AllowedIpCreateDto.builder()
                    .ipAddress(ipAddress)
                    .merchantId(merchantId)
                    .build());
        }
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