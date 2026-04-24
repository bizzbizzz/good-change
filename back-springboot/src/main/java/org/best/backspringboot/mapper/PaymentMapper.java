package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.best.backspringboot.dto.payment.PaymentSearchDto;
import org.best.backspringboot.entity.Payment;
import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentMapper {
    List<Payment> findAll(PaymentSearchDto dto);
    long countAll(PaymentSearchDto dto);
    Optional<Payment> findById(Long paymentId);
    void updateStatus(Long paymentId, String status);
}