package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.entity.PasswordResetToken;
import java.util.Optional;

@Mapper
public interface PasswordResetTokenMapper {
    void insert(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(String token);
    void markUsed(String token);
    void deleteByMemberId(Long memberId);
}