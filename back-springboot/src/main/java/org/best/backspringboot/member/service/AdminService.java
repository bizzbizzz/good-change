package org.best.backspringboot.member.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.global.commonDTO.PageResponse;
import org.best.backspringboot.member.dto.admin.AdminResponseDto;
import org.best.backspringboot.member.dto.admin.AdminSearchDto;
import org.best.backspringboot.member.entity.Member;
import org.best.backspringboot.member.mapper.AdminMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final String ADMIN_ROLE_FIXED = "ADMIN";

    private static final String PW_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int TEMP_PW_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<AdminResponseDto> getAllAdmin(AdminSearchDto searchDto) {
        PageResponse<AdminResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(searchDto.getPage());
        pageResponse.setSize(searchDto.getSize());

        List<AdminResponseDto> content = adminMapper.selectAllAdmin(searchDto, ADMIN_ROLE_FIXED)
                .stream()
                .map(AdminResponseDto::from)
                .collect(Collectors.toList());

        long totalCount = adminMapper.countAll(searchDto, ADMIN_ROLE_FIXED);
        pageResponse.setPageInfo(content, totalCount);
        return pageResponse;
    }

    @Transactional(readOnly = true)
    public AdminResponseDto getAdminById(Long id) {
        Member entity = adminMapper.selectAdminById(id);
        if (entity == null) {
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }
        return AdminResponseDto.from(entity);
    }

    @Transactional
    public void updateRole(Long id, String role) {
        Member entity = adminMapper.selectAdminById(id);
        if (entity == null) {
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }
        adminMapper.updateRole(id, role);
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        Member entity = adminMapper.selectAdminById(id);
        if (entity == null) {
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }
        adminMapper.updateStatus(id, status);
    }

    @Transactional
    public String resetPassword(Long id) {
        Member entity = adminMapper.selectAdminById(id);
        if (entity == null) {
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }

        String tempPassword = generateRandomPassword();
        adminMapper.updatePassword(id, passwordEncoder.encode(tempPassword));
        return tempPassword;
    }

    @Transactional
    public void deleteAdmin(Long id) {
        Member entity = adminMapper.selectAdminById(id);
        if (entity == null) {
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }
        adminMapper.deleteAdmin(id);
    }

    /**
     * 영문 대/소문자 + 숫자 + 특수문자가 섞인 10자리 랜덤 비밀번호 생성
     */
    private String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PW_LENGTH);
        for (int i = 0; i < TEMP_PW_LENGTH; i++) {
            sb.append(PW_CHARS.charAt(RANDOM.nextInt(PW_CHARS.length())));
        }
        return sb.toString();
    }
}