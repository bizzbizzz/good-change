package org.best.backspringboot.exception;

import lombok.Getter;
import org.best.backspringboot.dto.excel.ExcelUploadResultDto;

@Getter
public class BulkUploadException extends RuntimeException {
    private final ExcelUploadResultDto result;

    public BulkUploadException(ExcelUploadResultDto result) {
        super("일괄등록 실패");
        this.result = result;
    }
}