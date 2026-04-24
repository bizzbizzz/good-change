package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.dto.SearchBase;
import org.best.backspringboot.dto.member.MemberCreateDto;
import org.best.backspringboot.dto.member.MemberUpdateDto;
import org.best.backspringboot.entity.Member;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {
    void insert(MemberCreateDto dto);
    Optional<Member> findById(Long memberId);
    List<Member> findAll(SearchBase searchBase);
    void update(String loginId, MemberUpdateDto dto);
    void delete(String loginId);
    Optional<Member> findByLoginId(String loginId);
    long countAll();
    List<Member> findLeaveList(SearchBase searchBase);
    long countLeave();
}