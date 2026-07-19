package org.best.backspringboot.board.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    private Long boardId;
    private Long boardTypeId;
    private Long memberId;
    private String title;
    private String content;
    private String thumbnail;
    private String source;
    private String sourceUrl;
    private Integer viewCount;
    private Integer isPinned;
    private Integer isActive;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // JOIN 필드
    private String typeCode;
    private String typeName;
    private String memberName;
}
