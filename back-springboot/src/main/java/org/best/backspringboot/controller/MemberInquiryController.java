package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryRequestDto;
import org.best.backspringboot.dto.memberInquiry.MemberInquiryResponseDto;
import org.best.backspringboot.service.MemberInquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원가입 문의", description = "회원가입 문의 관련 API")
@RestController
@RequestMapping("/api/inquiry")
@RequiredArgsConstructor
public class MemberInquiryController {

    private final MemberInquiryService memberInquiryService;

    @Operation(summary = "문의 등록")
    @PostMapping
    public ResponseEntity<Void> insertInquiry(@RequestBody MemberInquiryRequestDto dto) {
        memberInquiryService.insertInquiry(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "문의 전체 조회")
    @GetMapping
    public ResponseEntity<List<MemberInquiryResponseDto>> getAllInquiry() {
        return ResponseEntity.ok(memberInquiryService.getAllInquiry());
    }

    @Operation(summary = "문의 단건 조회")
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<MemberInquiryResponseDto> getInquiryById(@PathVariable Long id) {
        return ResponseEntity.ok(memberInquiryService.getInquiryById(id));
    }

}