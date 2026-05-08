package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.dto.allowedip.AllowedIpCreateDto;
import org.best.backspringboot.entity.AllowedIp;
import java.util.List;

@Mapper
public interface AllowedIpMapper {
    void insert(AllowedIpCreateDto dto);
    List<AllowedIp> findAll();
    boolean existsByIp(String ipAddress);
    void deleteById(Long ipId);
}