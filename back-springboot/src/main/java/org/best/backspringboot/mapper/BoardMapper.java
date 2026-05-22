package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.dto.board.BoardCreateDto;
import org.best.backspringboot.dto.board.BoardSearchDto;
import org.best.backspringboot.dto.board.BoardUpdateDto;
import org.best.backspringboot.entity.Board;
import org.best.backspringboot.entity.CommonFile;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {
    void insert(BoardCreateDto dto);
    Optional<Board> findById(Long boardId);
    List<Board> findAll(BoardSearchDto dto);
    long countAll(BoardSearchDto dto);
    void update(@Param("boardId") Long boardId, @Param("dto") BoardUpdateDto dto);
    void delete(Long boardId);
    void increaseViewCount(Long boardId);

    // 파일
    void insertFile(CommonFile file);
    List<CommonFile> findFilesByRef(@Param("refType") String refType, @Param("refId") Long refId);
    void deleteFile(Long fileId);
    void deleteFilesByRef(@Param("refType") String refType, @Param("refId") Long refId);

    // 게시판 타입
    List<Board> findBoardTypes();
}
