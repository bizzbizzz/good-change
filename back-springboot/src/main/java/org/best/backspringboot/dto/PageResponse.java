package org.best.backspringboot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> extends SearchBase {
    private List<T> content;
    private long totalCount;
    private int totalPages;

    public void setPageInfo(List<T> content, long totalCount) {
        this.content = content;
        this.totalCount = totalCount;
        this.totalPages = (int) Math.ceil((double) totalCount / getSize());
    }
}