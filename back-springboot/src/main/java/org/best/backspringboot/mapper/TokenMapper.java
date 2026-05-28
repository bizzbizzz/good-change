package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenMapper {
    void upsert(Long memberId, String token);
    String findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);
}