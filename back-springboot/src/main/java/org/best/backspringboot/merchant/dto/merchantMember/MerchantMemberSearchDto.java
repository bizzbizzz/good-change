package org.best.backspringboot.merchant.dto.merchantMember;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.global.commonDTO.SearchBase;

@Getter
@Setter
public class MerchantMemberSearchDto extends SearchBase {
    private Long merchantId;
    private String merchantName;
    private String name;
    private String loginId;
    private String role;    // OWNER, STAFF
    private String status;  // ACTIVE, DISABLED
}