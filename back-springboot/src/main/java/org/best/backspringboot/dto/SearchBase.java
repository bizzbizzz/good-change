package org.best.backspringboot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchBase {
    private int page = 1;
    private int size = 10;
    private static final int MAX_SIZE = 100;  // 최대 100건

    public int getOffset() {
        int safePage = Math.max(page, 1);
        return (safePage - 1) * size;
    }

    public int getSize() {
        // size가 MAX_SIZE 초과하면 MAX_SIZE로 제한
        return Math.min(size, MAX_SIZE);
    }
}