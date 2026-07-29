package org.best.backspringboot.member.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.member.entity.Role;
import org.best.backspringboot.member.mapper.RoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<Role> getAll() {
        return roleMapper.findAll();
    }

    @Transactional(readOnly = true)
    public Role getById(Long roleId) {
        return roleMapper.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역할입니다."));
    }

    @Transactional(readOnly = true)
    public Role getByRoleName(String roleName) {
        return roleMapper.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역할입니다."));
    }
}