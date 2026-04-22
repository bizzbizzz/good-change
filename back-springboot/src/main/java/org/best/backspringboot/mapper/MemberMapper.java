package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.dto.member.MemberCreateDto;
import org.best.backspringboot.dto.member.MemberUpdateDto;
import org.best.backspringboot.entity.Member;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {
    void insert(MemberCreateDto dto);
    Optional<Member> findById(Long memberId);
    List<Member> findAll();
    void update(Long memberId, MemberUpdateDto dto);
    void delete(Long memberId);
    Optional<Member> findByLoginId(String loginId);
}