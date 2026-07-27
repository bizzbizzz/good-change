package org.best.backspringboot.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.member.entity.MemberWithdrawLog;

@Mapper
public interface MemberWithdrawLogMapper {
    void insert(MemberWithdrawLog log);
}