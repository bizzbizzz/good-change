package org.best.backspringboot.card.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.best.backspringboot.commonDTO.PageResponse;
import org.best.backspringboot.card.dto.point.PointGrantLogResponseDto;
import org.best.backspringboot.card.dto.point.PointGrantLogSearchDto;
import org.best.backspringboot.card.dto.point.PointGrantLogSummaryDto;
import org.best.backspringboot.card.dto.point.PointGrantRequestDto;
import org.best.backspringboot.member.entity.Member;
import org.best.backspringboot.card.entity.PointGrantLog;
import org.best.backspringboot.member.mapper.MemberMapper;
import org.best.backspringboot.card.mapper.PointGrantLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointGrantService {

    private final PointGrantLogMapper pointGrantLogMapper;
    private final MemberMapper        memberMapper;

    // ── 포인트 일괄지급 ──────────────────────────────────
    @Transactional
    public List<PointGrantLogResponseDto> grantPoints(PointGrantRequestDto dto, Long adminMemberId) {

        String grantId = UUID.randomUUID().toString(); // 배치 식별자
        List<PointGrantLogResponseDto> results = new ArrayList<>();

        for (Long memberId : dto.getMemberIds()) {
            PointGrantLog grantLog = PointGrantLog.builder()
                    .grantId(grantId)
                    .title(dto.getTitle())
                    .grantAmount(dto.getGrantAmount())
                    .memberId(memberId)
                    .grantedBy(adminMemberId)
                    .build();

            try {
                Member member = memberMapper.findById(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

                if (!"ACTIVE".equals(member.getStatus())) {
                    throw new IllegalArgumentException("비활성 회원입니다.");
                }

                long beforePoint = member.getPoint() != null ? member.getPoint() : 0L;
                long afterPoint  = beforePoint + dto.getGrantAmount();

                // member 포인트 업데이트
                memberMapper.updatePoint(memberId, afterPoint);

                grantLog.setMemberName(member.getName());
                grantLog.setBeforePoint(beforePoint);
                grantLog.setAfterPoint(afterPoint);
                grantLog.setStatus("SUCCESS");

            } catch (Exception e) {
                grantLog.setMemberName(grantLog.getMemberName() != null ? grantLog.getMemberName() : "알 수 없음");
                grantLog.setBeforePoint(0L);
                grantLog.setAfterPoint(0L);
                grantLog.setStatus("FAIL");
                grantLog.setFailReason(e.getMessage());
                log.warn("[PointGrant] 지급 실패 memberId={} reason={}", memberId, e.getMessage());
            }

            pointGrantLogMapper.insert(grantLog);
            results.add(PointGrantLogResponseDto.from(grantLog));
        }

        return results;
    }

    // ── 배치 집계 목록 조회 (지급 이력 탭) ───────────────
    @Transactional(readOnly = true)
    public PageResponse<PointGrantLogSummaryDto> getSummaryList(PointGrantLogSearchDto searchDto) {
        List<PointGrantLogSummaryDto> content = pointGrantLogMapper.findSummaryAll(searchDto);
        long totalCount = pointGrantLogMapper.countSummaryAll(searchDto);

        PageResponse<PointGrantLogSummaryDto> response = new PageResponse<>();
        response.setPage(searchDto.getPage());
        response.setSize(searchDto.getSize());
        response.setPageInfo(content, totalCount);
        return response;
    }

    // ── 배치 단위 상세 로그 조회 ─────────────────────────
    @Transactional(readOnly = true)
    public List<PointGrantLogResponseDto> getDetailByGrantId(String grantId) {
        return pointGrantLogMapper.findByGrantId(grantId).stream()
                .map(PointGrantLogResponseDto::from)
                .collect(Collectors.toList());
    }

    // ── 개별 로그 전체 조회 (검색+페이징) ─────────────────
    @Transactional(readOnly = true)
    public PageResponse<PointGrantLogResponseDto> getLogList(PointGrantLogSearchDto searchDto) {
        List<PointGrantLogResponseDto> content = pointGrantLogMapper.findAll(searchDto).stream()
                .map(PointGrantLogResponseDto::from)
                .collect(Collectors.toList());
        long totalCount = pointGrantLogMapper.countAll(searchDto);

        PageResponse<PointGrantLogResponseDto> response = new PageResponse<>();
        response.setPage(searchDto.getPage());
        response.setSize(searchDto.getSize());
        response.setPageInfo(content, totalCount);
        return response;
    }
}
