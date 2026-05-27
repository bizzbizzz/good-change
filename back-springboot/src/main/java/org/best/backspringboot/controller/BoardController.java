package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.board.BoardCreateDto;
import org.best.backspringboot.dto.board.BoardResponseDto;
import org.best.backspringboot.dto.board.BoardSearchDto;
import org.best.backspringboot.dto.board.BoardUpdateDto;
import org.best.backspringboot.entity.Board;
import org.best.backspringboot.entity.CommonFile;
import org.best.backspringboot.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Tag(name = "게시판", description = "게시판 관련 API")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private static final String UPLOAD_PATH = System.getProperty("user.dir") + "/src/main/resources/uploads/board/";
    private static final String UPLOAD_URL  = "/uploads/board/";

    @Operation(summary = "에디터 이미지 업로드")
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "boardType", defaultValue = "board") String boardType,
            @RequestParam(value = "boardId", defaultValue = "0") Long boardId) throws Exception {

        String ext        = getExt(file.getOriginalFilename());
        String storedName = UUID.randomUUID().toString() + "." + ext;
        String uploadPath = UPLOAD_PATH + boardType + "/";
        String uploadUrl  = UPLOAD_URL  + boardType + "/" + storedName;

        new File(uploadPath).mkdirs();
        file.transferTo(new File(uploadPath + storedName));

        CommonFile commonFile = CommonFile.builder()
                .refType(boardType.toLowerCase() + "_editor")  // ex) notice_editor
                .refId(boardId)
                .fileName(file.getOriginalFilename())
                .storedName(storedName)
                .filePath(uploadPath + storedName)
                .fileExt(ext)
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .sortOrder(0)
                .build();

        boardService.addFile(commonFile);
        return ResponseEntity.ok(Map.of("url", uploadUrl));
    }

    @Operation(summary = "게시글 목록 조회")
    @GetMapping
    public ResponseEntity<PageResponse<BoardResponseDto>> getAll(BoardSearchDto dto) {
        return ResponseEntity.ok(boardService.getAll(dto));
    }

    @Operation(summary = "게시글 단건 조회")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getById(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getById(boardId));
    }

    @Operation(summary = "게시판 타입 목록")
    @GetMapping("/types")
    public ResponseEntity<List<Board>> getBoardTypes() {
        return ResponseEntity.ok(boardService.getBoardTypes());
    }

    @Operation(summary = "게시글 등록")
    @PostMapping
    public ResponseEntity<Map<String, Long>> create(@Valid @RequestBody BoardCreateDto dto) {
        Long boardId = boardService.create(dto);
        return ResponseEntity.ok(Map.of("boardId", boardId));
    }

    @Operation(summary = "게시글 수정")
    @PatchMapping("/{boardId}")
    public ResponseEntity<Void> update(@PathVariable Long boardId,
                                       @RequestBody BoardUpdateDto dto) {
        boardService.update(boardId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(@PathVariable Long boardId) {
        boardService.delete(boardId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "파일 업로드")
    @PostMapping("/{boardId}/files")
    public ResponseEntity<Void> uploadFile(@PathVariable Long boardId,
                                           @RequestParam("file") MultipartFile file) throws Exception {

        String boardType  = boardService.getBoardTypeCode(boardId);
        String ext        = getExt(file.getOriginalFilename());
        String storedName = UUID.randomUUID().toString() + "." + ext;
        String uploadPath = UPLOAD_PATH + boardType + "/";
        String filePath   = uploadPath + storedName;

        new File(uploadPath).mkdirs();
        file.transferTo(new File(filePath));

        CommonFile commonFile = CommonFile.builder()
                .refType(boardType.toLowerCase() + "_thumbnail")  // ex) notice_thumbnail
                .refId(boardId)
                .fileName(file.getOriginalFilename())
                .storedName(storedName)
                .filePath(filePath)
                .fileExt(ext)
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .sortOrder(0)
                .build();

        boardService.addFile(commonFile);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "파일 삭제")
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        // 실제 파일도 삭제
        CommonFile file = boardService.getFile(fileId);
        if (file != null && file.getFilePath() != null) {
            new File(file.getFilePath()).delete();
        }
        boardService.deleteFile(fileId);
        return ResponseEntity.ok().build();
    }

    private String getExt(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}