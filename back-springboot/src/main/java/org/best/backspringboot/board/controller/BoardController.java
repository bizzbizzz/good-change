package org.best.backspringboot.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.global.commonDTO.PageResponse;
import org.best.backspringboot.board.dto.board.BoardCreateDto;
import org.best.backspringboot.board.dto.board.BoardResponseDto;
import org.best.backspringboot.board.dto.board.BoardSearchDto;
import org.best.backspringboot.board.dto.board.BoardUpdateDto;
import org.best.backspringboot.board.entity.Board;
import org.best.backspringboot.board.dto.file.CommonFile;
import org.best.backspringboot.board.service.BoardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "게시판", description = "게시판 관련 API")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    @Value("${file.upload-path}")
    private String UPLOAD_PATH;

    @Value("${file.upload-url}")
    private String UPLOAD_URL;


    // 에디터 이미지 업로드 (공통)
    @Operation(summary = "에디터 이미지 업로드",
            description = "WYSIWYG 에디터용 이미지 업로드. png/jpeg/gif/webp만 허용.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공 (url 반환)"),
            @ApiResponse(responseCode = "400", description = "이미지 파일이 아님", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "boardType", defaultValue = "board") String boardType,
            @RequestParam(value = "boardId", defaultValue = "0") Long boardId) throws Exception {

        List<String> allowedTypes = List.of("image/png", "image/jpeg", "image/gif", "image/webp");
        if (!allowedTypes.contains(file.getContentType())) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미지 파일만 업로드 가능합니다."));
        }

        String ext        = getExt(file.getOriginalFilename());
        String storedName = UUID.randomUUID().toString() + "." + ext;
        String uploadPath = UPLOAD_PATH + boardType + "/";
        String uploadUrl  = UPLOAD_URL  + boardType + "/" + storedName;

        new File(uploadPath).mkdirs();
        file.transferTo(new File(uploadPath + storedName));

        CommonFile commonFile = CommonFile.builder()
                .refType(boardType.toLowerCase() + "_editor")
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

    // 썸네일 업로드 (언론보도 전용)
    @Operation(summary = "썸네일 업로드", description = "언론보도 게시글 썸네일 업로드.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미지 파일이 아님", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/{boardId}/thumbnail")
    public ResponseEntity<Void> uploadThumbnail(@PathVariable Long boardId,
                                                @RequestParam("file") MultipartFile file) throws Exception {

        List<String> allowedTypes = List.of("image/png", "image/jpeg", "image/gif", "image/webp");
        if (!allowedTypes.contains(file.getContentType())) {
            return ResponseEntity.badRequest().build();
        }

        String boardType  = boardService.getBoardTypeCode(boardId);
        String ext        = getExt(file.getOriginalFilename());
        String storedName = UUID.randomUUID().toString() + "." + ext;
        String uploadPath = UPLOAD_PATH + boardType + "/";
        String filePath   = uploadPath + storedName;

        new File(uploadPath).mkdirs();
        file.transferTo(new File(filePath));

        // board.thumbnail 컬럼 업데이트
        boardService.updateThumbnail(boardId, UPLOAD_URL + boardType + "/" + storedName);
        return ResponseEntity.ok().build();
    }

    // 썸네일 삭제
    @Operation(summary = "썸네일 삭제")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{boardId}/thumbnail")
    public ResponseEntity<Void> deleteThumbnail(@PathVariable Long boardId) {
        boardService.deleteThumbnail(boardId);
        return ResponseEntity.ok().build();
    }

    // 첨부파일 업로드 (서식/자료 전용)
    @Operation(summary = "첨부파일 업로드", description = "서식/자료 게시글 첨부파일 업로드.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "허용되지 않는 파일", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/{boardId}/files")
    public ResponseEntity<Void> uploadFile(@PathVariable Long boardId,
                                           @RequestParam("file") MultipartFile file) throws Exception {

        List<String> allowedTypes = List.of("image/png", "image/jpeg", "image/gif", "image/webp");
        if (!allowedTypes.contains(file.getContentType())) {
            return ResponseEntity.badRequest().build();
        }

        String boardType  = boardService.getBoardTypeCode(boardId);
        String ext        = getExt(file.getOriginalFilename());
        String storedName = UUID.randomUUID().toString() + "." + ext;
        String uploadPath = UPLOAD_PATH + boardType + "/";
        String filePath   = uploadPath + storedName;

        new File(uploadPath).mkdirs();
        file.transferTo(new File(filePath));

        CommonFile commonFile = CommonFile.builder()
                .refType(boardType.toLowerCase() + "_attach")  // resource_attach
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

    @Operation(summary = "게시글 목록 조회")
    @GetMapping
    public ResponseEntity<PageResponse<BoardResponseDto>> getAll(BoardSearchDto dto) {
        return ResponseEntity.ok(boardService.getAll(dto));
    }

    @Operation(summary = "게시글 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음", content = @Content)
    })
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getById(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getById(boardId));
    }

    @Operation(summary = "게시판 타입 목록")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/types")
    public ResponseEntity<List<Board>> getBoardTypes() {
        return ResponseEntity.ok(boardService.getBoardTypes());
    }

    @Operation(summary = "게시글 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공 (boardId 반환)"),
            @ApiResponse(responseCode = "400", description = "검증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<Map<String, Long>> create(@Valid @RequestBody BoardCreateDto dto) {
        Long boardId = boardService.create(dto);
        return ResponseEntity.ok(Map.of("boardId", boardId));
    }

    @Operation(summary = "게시글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping("/{boardId}")
    public ResponseEntity<Void> update(@PathVariable Long boardId,
                                       @RequestBody BoardUpdateDto dto) {
        boardService.update(boardId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "에디터 이미지 초기화", description = "게시글에 연결된 에디터 업로드 이미지를 모두 삭제합니다.")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{boardId}/editor-images")
    public ResponseEntity<Void> deleteEditorImages(@PathVariable Long boardId) {
        boardService.deleteEditorImages(boardId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 삭제")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(@PathVariable Long boardId) {
        boardService.delete(boardId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "첨부파일 삭제", description = "파일 ID로 첨부파일을 삭제합니다 (물리 파일 + DB).")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        CommonFile file = boardService.getFile(fileId);
        if (file != null && file.getFilePath() != null) {
            new File(file.getFilePath()).delete();
        }
        boardService.deleteFile(fileId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "파일 다운로드", description = "파일 ID로 첨부파일을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공 (파일 스트림)"),
            @ApiResponse(responseCode = "404", description = "파일 없음", content = @Content)
    })
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws Exception {
        CommonFile file = boardService.getFile(fileId);
        if (file == null) return ResponseEntity.notFound().build();

        Path path = Paths.get(file.getFilePath());
        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                URLEncoder.encode(file.getFileName(), "UTF-8") + "\"")
                .header(HttpHeaders.CONTENT_TYPE,
                        file.getMimeType() != null ? file.getMimeType() : "application/octet-stream")
                .body(resource);
    }

    private String getExt(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}