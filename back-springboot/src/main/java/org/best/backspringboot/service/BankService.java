package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.entity.Bank;
import org.best.backspringboot.mapper.BankMapper;
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
}
