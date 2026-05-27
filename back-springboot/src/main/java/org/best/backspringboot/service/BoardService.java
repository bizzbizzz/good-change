package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.board.BoardCreateDto;
import org.best.backspringboot.dto.board.BoardResponseDto;
import org.best.backspringboot.dto.board.BoardSearchDto;
import org.best.backspringboot.dto.board.BoardUpdateDto;
import org.best.backspringboot.entity.Board;
import org.best.backspringboot.entity.CommonFile;
import org.best.backspringboot.mapper.BoardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;

    @Transactional
    public void deleteThumbnail(Long boardId) {
        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        String boardType = board.getTypeCode() != null
                ? board.getTypeCode().toLowerCase() : "board";

        List<CommonFile> thumbFiles = boardMapper.findFilesByRef(boardType + "_thumbnail", boardId);
        for (CommonFile f : thumbFiles) {
            if (f.getFilePath() != null) new File(f.getFilePath()).delete();
        }
        boardMapper.deleteFilesByRef(boardType + "_thumbnail", boardId);

        // board.thumbnail 초기화 추가
        BoardUpdateDto dto = new BoardUpdateDto();
        dto.setThumbnail(null);
        boardMapper.update(boardId, dto);
    }

    @Transactional(readOnly = true)
    public PageResponse<BoardResponseDto> getAll(BoardSearchDto dto) {
        PageResponse<BoardResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(dto.getPage());
        pageResponse.setSize(dto.getSize());

        List<BoardResponseDto> content = boardMapper.findAll(dto).stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());

        long totalCount = boardMapper.countAll(dto);
        pageResponse.setPageInfo(content, totalCount);
        return pageResponse;
    }
    @Transactional(readOnly = true)
    public String getBoardTypeCode(Long boardId) {
        return boardMapper.findById(boardId)
                .map(b -> b.getTypeCode() != null ? b.getTypeCode() : "board")
                .orElse("board");
    }

    // BoardService에 추가
    @Transactional
    public void deleteEditorImages(Long boardId) {
        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        String boardType = board.getTypeCode() != null
                ? board.getTypeCode().toLowerCase() : "board";

        List<CommonFile> editorFiles = boardMapper.findFilesByRef(boardType + "_editor", boardId);
        for (CommonFile f : editorFiles) {
            if (f.getFilePath() != null) new File(f.getFilePath()).delete();
        }
        boardMapper.deleteFilesByRef(boardType + "_editor", boardId);
    }

    @Transactional(readOnly = true)
    public CommonFile getFile(Long fileId) {
        return boardMapper.findFileById(fileId);
    }

    @Transactional
    public BoardResponseDto getById(Long boardId) {
        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        boardMapper.increaseViewCount(boardId);

        String boardType = board.getTypeCode() != null
                ? board.getTypeCode().toLowerCase() : "board";

        // thumbnail 파일만 조회 (에디터 이미지 제외)
        List<CommonFile> files = boardMapper.findFilesByRef(boardType + "_thumbnail", boardId);
        return BoardResponseDto.from(board, files);
    }

    @Transactional
    public Long create(BoardCreateDto dto) {
        boardMapper.insert(dto);
        return dto.getBoardId();
    }

    @Transactional
    public void update(Long boardId, BoardUpdateDto dto) {
        boardMapper.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        boardMapper.update(boardId, dto);
    }

    @Transactional
    public void delete(Long boardId) {
        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        String boardType = board.getTypeCode() != null
                ? board.getTypeCode().toLowerCase() : "board";

        // 에디터 이미지 파일 삭제
        List<CommonFile> editorFiles = boardMapper.findFilesByRef(boardType + "_editor", boardId);
        for (CommonFile f : editorFiles) {
            if (f.getFilePath() != null) new File(f.getFilePath()).delete();
        }
        boardMapper.deleteFilesByRef(boardType + "_editor", boardId);

        // 첨부파일 삭제
        List<CommonFile> thumbFiles = boardMapper.findFilesByRef(boardType + "_thumbnail", boardId);
        for (CommonFile f : thumbFiles) {
            if (f.getFilePath() != null) new File(f.getFilePath()).delete();
        }
        boardMapper.deleteFilesByRef(boardType + "_thumbnail", boardId);

        boardMapper.delete(boardId);
    }

    @Transactional
    public void updateThumbnail(Long boardId, String thumbnailUrl) {
        BoardUpdateDto dto = new BoardUpdateDto();
        dto.setThumbnail(thumbnailUrl);
        boardMapper.update(boardId, dto);
    }



    @Transactional(readOnly = true)
    public List<Board> getBoardTypes() {
        return boardMapper.findBoardTypes();
    }

    // 파일 등록
    @Transactional
    public void addFile(CommonFile file) {
        boardMapper.insertFile(file);
    }

    // 파일 삭제
    @Transactional
    public void deleteFile(Long fileId) {
        boardMapper.deleteFile(fileId);
    }
}
