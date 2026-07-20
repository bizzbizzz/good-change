package org.best.backspringboot.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.member.dto.admin.AdminSearchDto;
import org.best.backspringboot.member.entity.Member;

import java.util.List;

@Mapper
public interface AdminMapper {

    List<Member> selectAllAdmin(@Param("search") AdminSearchDto searchDto,
                                @Param("roleFixed") String roleFixed);

    long countAll(@Param("search") AdminSearchDto searchDto,
                  @Param("roleFixed") String roleFixed);

    Member selectAdminById(@Param("memberId") Long memberId);

    void updateRole(@Param("memberId") Long memberId, @Param("role") String role);

    void updateStatus(@Param("memberId") Long memberId, @Param("status") String status);

    void updatePassword(@Param("memberId") Long memberId, @Param("password") String encodedPassword);

    void deleteAdmin(@Param("memberId") Long memberId);
}