package org.best.backspringboot.card.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.card.entity.Bank;
import org.best.backspringboot.card.mapper.BankMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankMapper bankMapper;

    @Transactional(readOnly = true)
    public List<Bank> getAll() {
        return bankMapper.findAll();
    }

    @Transactional(readOnly = true)
    public Bank getById(Long bankId) {
        return bankMapper.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 은행입니다."));
    }

    @Transactional
    public void create(String bankName, String bankCode) {
        if (bankName == null || bankName.isBlank())
            throw new IllegalArgumentException("은행명은 필수입니다.");
        if (bankCode == null || bankCode.isBlank())
            throw new IllegalArgumentException("은행코드는 필수입니다.");
        bankMapper.insert(bankName, bankCode);
    }

    @Transactional
    public void update(Long bankId, String bankName, String bankCode, String status) {
        bankMapper.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 은행입니다."));
        bankMapper.update(bankId, bankName, bankCode, status);
    }

    @Transactional
    public void delete(Long bankId) {
        bankMapper.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 은행입니다."));
        bankMapper.delete(bankId);
    }
}