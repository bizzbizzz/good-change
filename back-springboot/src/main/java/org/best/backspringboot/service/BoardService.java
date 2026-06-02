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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;
    @Value("${file.upload-path}")
    private String UPLOAD_PATH;

    @Value("${file.upload-url}")
    private String UPLOAD_URL;


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

        // RESOURCE만 첨부파일 조회, PRESS는 board.thumbnail 사용
        List<CommonFile> files = ("resource".equals(boardType) || "press".equals(boardType))
                ? boardMapper.findFilesByRef(boardType + "_attach", boardId)
                : List.of();

        return BoardResponseDto.from(board, files);
    }

    @Transactional
    public Long create(BoardCreateDto dto) {
        boardMapper.insert(dto);
        return dto.getBoardId();
    }

    @Transactional
    public void update(Long boardId, BoardUpdateDto dto) {
        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        String boardType = board.getTypeCode() != null
                ? board.getTypeCode().toLowerCase() : "board";

        // content에서 사용 중인 이미지 storedName 추출
        if (dto.getContent() != null) {
            List<CommonFile> editorFiles = boardMapper.findFilesByRef(boardType + "_editor", boardId);
            Set<String> usedNames = new HashSet<>();

            // content에서 storedName 추출
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("/uploads/board/[^/]+/([^\"\\s]+)");
            java.util.regex.Matcher matcher = pattern.matcher(dto.getContent());
            while (matcher.find()) {
                usedNames.add(matcher.group(1));
            }

            // content에 없는 에디터 이미지만 삭제
            for (CommonFile f : editorFiles) {
                if (!usedNames.contains(f.getStoredName())) {
                    if (f.getFilePath() != null) new File(f.getFilePath()).delete();
                    boardMapper.deleteFile(f.getFileId());
                }
            }
        }

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

        // 첨부파일 삭제 (서식/자료)
        List<CommonFile> attachFiles = boardMapper.findFilesByRef(boardType + "_attach", boardId);
        for (CommonFile f : attachFiles) {
            if (f.getFilePath() != null) new File(f.getFilePath()).delete();
        }
        boardMapper.deleteFilesByRef(boardType + "_attach", boardId);


        // PRESS 썸네일 실제 파일 삭제
        if ("press".equals(boardType) && board.getThumbnail() != null) {
            // DB엔 URL(/uploads/board/PRESS/xxx.png)로 저장됨 → 실제 경로로 변환
            String filePath = board.getThumbnail()
                    .replace(UPLOAD_URL, UPLOAD_PATH);
            new File(filePath).delete();
        }

        boardMapper.delete(boardId);
    }

    @Transactional
    public void updateThumbnail(Long boardId, String thumbnailUrl) {
        BoardUpdateDto dto = new BoardUpdateDto();
        dto.setThumbnail(thumbnailUrl);
        boardMapper.update(boardId, dto);
    }

    @Transactional
    public void deleteThumbnail(Long boardId) {
        Board board = boardMapper.findById(boardId).orElse(null);
        if (board != null && board.getThumbnail() != null) {
            String filePath = board.getThumbnail().replace(UPLOAD_URL, UPLOAD_PATH);
            new File(filePath).delete();
        }
        boardMapper.clearThumbnail(boardId);
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
