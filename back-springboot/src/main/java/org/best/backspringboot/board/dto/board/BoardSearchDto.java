package org.best.backspringboot.board.dto.board;

import lombok.Getter;
import lombok.Setter;
import org.best.backspringboot.global.commonDTO.SearchBase;

@Getter
@Setter
public class BoardSearchDto extends SearchBase {
    private String typeCode;    // NOTICE, RESOURCE, PRESS
    private String keyword;     // 제목+내용 검색
    private String title;       // 제목 검색
    private Integer isPinned;   // 상단고정 여부
}
