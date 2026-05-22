package org.best.backspringboot.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCreateDto {

    @NotNull
    private Long boardTypeId;
    private Long boardId;

    private Long memberId;

    @NotBlank
    private String title;

    private String content;
    private String thumbnail;
    private String source;
    private String sourceUrl;
    private Integer isPinned = 0;
    private Integer isActive = 1;


}
