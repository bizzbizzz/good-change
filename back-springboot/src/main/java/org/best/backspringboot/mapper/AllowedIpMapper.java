package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.dto.allowedip.AllowedIpCreateDto;
import org.best.backspringboot.entity.AllowedIp;
import java.util.List;
import java.util.Optional;

@Mapper
public interface AllowedIpMapper {
    void insert(AllowedIpCreateDto dto);
    List<AllowedIp> findAll();
    boolean existsByIp(String ipAddress);
    void deleteById(Long ipId);
    Optional<AllowedIp> findByMerchantId(Long merchantId);
    void updateByMerchantId(@Param("merchantId") Long merchantId, @Param("ipAddress") String ipAddress);
}