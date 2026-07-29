package org.best.backspringboot.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.member.entity.Role;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoleMapper {
    List<Role> findAll();
    Optional<Role> findById(Long roleId);
    Optional<Role> findByRoleName(String roleName);
}