package org.best.backspringboot.siteConfig.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.best.backspringboot.commonDTO.PageResponse;
import org.best.backspringboot.siteConfig.dto.banner.BannerCreateDto;
import org.best.backspringboot.siteConfig.dto.banner.BannerResponseDto;
import org.best.backspringboot.siteConfig.dto.banner.BannerSearchDto;
import org.best.backspringboot.siteConfig.dto.banner.BannerUpdateDto;
import org.best.backspringboot.siteConfig.entity.Banner;
import org.best.backspringboot.siteConfig.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerMapper bannerMapper;

    @Value("${file.upload-path}")
    private String UPLOAD_PATH;

    @Value("${file.upload-url}")
    private String UPLOAD_URL;

    // ── 전체 조회 (검색 + 페이징) ──────────────────────────
    @Transactional(readOnly = true)
    public PageResponse<BannerResponseDto> getAll(BannerSearchDto searchDto) {
        List<BannerResponseDto> content = bannerMapper.findAll(searchDto).stream()
                .map(BannerResponseDto::from)
                .collect(Collectors.toList());

        long totalCount = bannerMapper.countAll(searchDto);

        PageResponse<BannerResponseDto> response = new PageResponse<>();
        response.setPage(searchDto.getPage());
        response.setSize(searchDto.getSize());
        response.setPageInfo(content, totalCount);
        return response;
    }

    // ── 단건 조회 ───────────────────────────────────────────
    @Transactional(readOnly = true)
    public BannerResponseDto getById(Long bannerId) {
        return bannerMapper.findById(bannerId)
                .map(BannerResponseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다."));
    }

    // ── 등록 ────────────────────────────────────────────────
    @Transactional
    public Long create(BannerCreateDto dto) throws Exception {
        String imageUrl = uploadImage(dto.getImageFile());

        Banner banner = Banner.builder()
                .title(dto.getTitle())
                .imageUrl(imageUrl)
                .linkUrl(dto.getLinkUrl())
                .sortNo(dto.getSortNo() != null ? dto.getSortNo() : 1)
                .useYn("Y")
                .build();

        bannerMapper.insert(banner);
        return banner.getBannerId();
    }

    // ── 수정 ────────────────────────────────────────────────
    @Transactional
    public void update(Long bannerId, BannerUpdateDto dto) throws Exception {
        Banner existing = bannerMapper.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다."));

        String imageUrl = null;

        // 새 이미지가 있으면 → 기존 파일 삭제 후 새 파일 업로드
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            deleteFile(existing.getImageUrl());         // 기존 파일 삭제
            imageUrl = uploadImage(dto.getImageFile()); // 새 파일 업로드
        }
        // 이미지 없으면 imageUrl = null → XML if 조건으로 기존 image_url 유지

        bannerMapper.update(bannerId, dto.getTitle(), dto.getLinkUrl(),
                dto.getSortNo(), dto.getUseYn(), imageUrl);
    }

    // ── 삭제 ────────────────────────────────────────────────
    @Transactional
    public void delete(Long bannerId) {
        Banner existing = bannerMapper.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다."));

        deleteFile(existing.getImageUrl()); // 서버 파일 삭제
        bannerMapper.delete(bannerId);      // DB 삭제
    }

    // ── 서버 파일 삭제 ───────────────────────────────────────
    // imageUrl 예: /uploads/board/banner/uuid.png
    // UPLOAD_URL 예: /uploads/board/
    // UPLOAD_PATH 예: /home/bizline/springboot/uploads/board/
    // → 파일경로: /home/bizline/springboot/uploads/board/banner/uuid.png
    private void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        try {
            // URL에서 UPLOAD_URL 앞부분을 UPLOAD_PATH로 치환해 실제 경로 획득
            String filePath = imageUrl.replace(UPLOAD_URL, UPLOAD_PATH);
            File file = new File(filePath);

            if (file.exists()) {
                if (file.delete()) {
                    log.info("[Banner] 파일 삭제 성공: {}", filePath);
                } else {
                    log.warn("[Banner] 파일 삭제 실패: {}", filePath);
                }
            } else {
                log.warn("[Banner] 삭제할 파일 없음: {}", filePath);
            }
        } catch (Exception e) {
            // 파일 삭제 실패가 전체 트랜잭션을 롤백시키지 않도록 예외 흡수
            log.error("[Banner] 파일 삭제 중 오류: {}", e.getMessage());
        }
    }

    // ── 이미지 업로드 공통 ───────────────────────────────────
    private String uploadImage(MultipartFile file) throws Exception {
        List<String> allowedTypes = List.of("image/png", "image/jpeg", "image/gif", "image/webp");
        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다. (png, jpg, gif, webp)");
        }

        String ext        = getExt(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + "." + ext;
        String uploadPath = UPLOAD_PATH + "banner/";
        String uploadUrl  = UPLOAD_URL  + "banner/" + storedName;

        new File(uploadPath).mkdirs();
        file.transferTo(new File(uploadPath + storedName));

        return uploadUrl;
    }

    private String getExt(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
