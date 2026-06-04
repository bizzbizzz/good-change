package org.best.backspringboot.dto.excel;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExcelUploadResultDto {
    private int          totalCount;    // 전체 행 수
    private int          successCount;  // 성공 건수
    private int          failCount;     // 실패 건수
    private List<String> errors;        // 실패 사유 (행번호 + 메시지)
}
