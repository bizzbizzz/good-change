package org.best.backspringboot.commonDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> extends SearchBase {
    private List<T> content;
    private long totalCount;
    private int totalPages;
    private long totalAmount;  // 추가
    private long successCount; // 추가

    public void setPageInfo(List<T> content, long totalCount) {
        this.content = content;
        this.totalCount = totalCount;
        this.totalPages = (int) Math.ceil((double) totalCount / getSize());
    }
}