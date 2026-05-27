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

    public String getBoardTypeCode(Long boardId) {
        return boardMapper.findById(boardId)
                .map(b -> b.getTypeCode() != null ? b.getTypeCode() : "board")
                .orElse("board");
    }

    public CommonFile getFile(Long fileId) {
        return boardMapper.findFileById(fileId);
    }

    @Transactional
    public BoardResponseDto getById(Long boardId) {
        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 조회수 증가
        boardMapper.increaseViewCount(boardId);

        // 첨부파일 조회
        List<CommonFile> files = boardMapper.findFilesByRef("board", boardId);
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

        // 썸네일 파일 삭제
        List<CommonFile> thumbFiles = boardMapper.findFilesByRef(boardType + "_thumbnail", boardId);
        for (CommonFile f : thumbFiles) {
            if (f.getFilePath() != null) new File(f.getFilePath()).delete();
        }
        boardMapper.deleteFilesByRef(boardType + "_thumbnail", boardId);

        // 게시글 삭제
        boardMapper.delete(boardId);
    }

    private void deleteImagesFromContent(String content) {
        try {
            var pattern = java.util.regex.Pattern.compile(
                    "src=\"[^\"]*\\/uploads\\/([^\"]+)\"");
            var matcher = pattern.matcher(content);
            while (matcher.find()) {
                String relativePath = matcher.group(1);
                String filePath = System.getProperty("user.dir")
                        + "/src/main/resources/uploads/" + relativePath;
                new File(filePath).delete();
            }
        } catch (Exception e) {
            // 파일 삭제 실패 시 무시
        }
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
