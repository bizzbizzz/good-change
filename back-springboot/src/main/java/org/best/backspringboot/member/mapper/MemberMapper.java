package org.best.backspringboot.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.member.dto.member.MemberCreateDto;
import org.best.backspringboot.member.dto.member.MemberSearchDto;
import org.best.backspringboot.member.dto.member.MemberUpdateDto;
import org.best.backspringboot.member.entity.Member;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {
    void insert(MemberCreateDto dto);
    Optional<Member> findById(Long memberId);
    List<Member> findAll(MemberSearchDto searchBase);
    void update(@Param("memberId") Long memberId, @Param("dto") MemberUpdateDto dto);
    void delete(Long memberId);
    Optional<Member> findByLoginId(String memberId);
    long countAll(MemberSearchDto searchDto);
    void updatePoint(Long memberId, Long point);

    String findRoleNameById(Long roleId);        // 로그인 시 role 조회
    void updateStatus(String loginId, String status); // soft delete용

    Optional<Member> findByEmail(String email);
    void updatePassword(@Param("memberId") Long memberId, @Param("password") String password);

    List<Member> findAllNoPaging(MemberSearchDto searchDto);
    void withdraw(Long memberId);  // ✅ 회원탈퇴 전용
    void updateStatusById(@Param("memberId") Long memberId, @Param("status") String status); // ✅ 추가
}