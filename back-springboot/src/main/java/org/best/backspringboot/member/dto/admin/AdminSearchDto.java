package org.best.backspringboot.member.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.global.commonDTO.SearchBase;

@Getter
@Setter
@Schema(description = "관리자 목록 검색 조건")
public class AdminSearchDto extends SearchBase {

    @Schema(description = "검색 조건 (name/loginId/email)", example = "name")
    private String searchType;

    @Schema(description = "검색어")
    private String keyword;

    @Schema(description = "상태 필터 (ACTIVE/SUSPENDED)")
    private String status;

    @Schema(description = "정렬 대상 컬럼", example = "regDate")
    private String sortField;

    @Schema(description = "정렬 방향 (asc/desc)", example = "desc")
    private String sortDirection = "desc";
}
