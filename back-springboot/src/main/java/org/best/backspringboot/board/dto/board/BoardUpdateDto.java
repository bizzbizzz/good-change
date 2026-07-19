package org.best.backspringboot.board.dto.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateDto {
    private String title;
    private String content;
    private String thumbnail;
    private String source;
    private String sourceUrl;
    private Integer isPinned;
    private Integer isActive;
}
