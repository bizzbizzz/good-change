package org.best.backspringboot.board.dto.board;

import lombok.Builder;
import lombok.Getter;
import org.best.backspringboot.board.entity.Board;
import org.best.backspringboot.board.dto.file.CommonFile;


import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BoardResponseDto {
    private Long boardId;
    private Long boardTypeId;
    private String typeCode;
    private String typeName;
    private Long memberId;
    private String memberName;
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
    private List<CommonFile> files;

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .boardId(board.getBoardId())
                .boardTypeId(board.getBoardTypeId())
                .typeCode(board.getTypeCode())
                .typeName(board.getTypeName())
                .memberId(board.getMemberId())
                .memberName(board.getMemberName())
                .title(board.getTitle())
                .content(board.getContent())
                .thumbnail(board.getThumbnail())
                .source(board.getSource())
                .sourceUrl(board.getSourceUrl())
                .viewCount(board.getViewCount())
                .isPinned(board.getIsPinned())
                .isActive(board.getIsActive())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

    public static BoardResponseDto from(Board board, List<CommonFile> files) {
        return BoardResponseDto.builder()
                .boardId(board.getBoardId())
                .boardTypeId(board.getBoardTypeId())
                .typeCode(board.getTypeCode())
                .typeName(board.getTypeName())
                .memberId(board.getMemberId())
                .memberName(board.getMemberName())
                .title(board.getTitle())
                .content(board.getContent())
                .thumbnail(board.getThumbnail())
                .source(board.getSource())
                .sourceUrl(board.getSourceUrl())
                .viewCount(board.getViewCount())
                .isPinned(board.getIsPinned())
                .isActive(board.getIsActive())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .files(files)
                .build();
    }
}
