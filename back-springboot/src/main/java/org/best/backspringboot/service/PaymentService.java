package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.best.backspringboot.dto.PageResponse;
import org.best.backspringboot.dto.payment.PaymentResponseDto;
import org.best.backspringboot.dto.payment.PaymentSearchDto;
import org.best.backspringboot.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponseDto> getAll(PaymentSearchDto dto) {
        PageResponse<PaymentResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(dto.getPage());
        pageResponse.setSize(dto.getSize());

        List<PaymentResponseDto> content = paymentMapper.findAll(dto).stream()
                .map(PaymentResponseDto::from)
                .collect(Collectors.toList());

        long totalCount = paymentMapper.countAll(dto);
        pageResponse.setPageInfo(content, totalCount);
        return pageResponse;
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getById(Long paymentId) {
        return paymentMapper.findById(paymentId)
                .map(PaymentResponseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제내역입니다."));
    }

    @Transactional
    public void cancel(Long paymentId) {
        paymentMapper.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제내역입니다."));
        paymentMapper.updateStatus(paymentId, "CANCELED");
    }
}