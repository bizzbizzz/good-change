package org.best.backspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.best.backspringboot.entity.Bank;
import org.best.backspringboot.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "은행", description = "은행 목록 API")
@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @Operation(summary = "은행 목록 조회")
    @GetMapping
    public ResponseEntity<List<Bank>> getAll() {
        return ResponseEntity.ok(bankService.getAll());
    }

    @Operation(summary = "은행 단건 조회")
    @GetMapping("/{bankId}")
    public ResponseEntity<Bank> getById(@PathVariable Long bankId) {
        return ResponseEntity.ok(bankService.getById(bankId));
    }
}
