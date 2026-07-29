package org.best.backspringboot.merchant.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.merchant.dto.merchantMember.MerchantMemberSearchDto;
import org.best.backspringboot.merchant.entity.MerchantMember;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MerchantMemberMapper {
    void insert(@Param("merchantId") Long merchantId,
                @Param("memberId") Long memberId,
                @Param("roleId") Long roleId);

    Optional<MerchantMember> findByMemberId(Long memberId);
    List<MerchantMember> findByMerchantId(Long merchantId);
    void deleteByMerchantId(Long merchantId);
    void deleteByMemberId(Long memberId);
    List<MerchantMember> findAll(MerchantMemberSearchDto searchDto);
    long countAll(MerchantMemberSearchDto searchDto);
}