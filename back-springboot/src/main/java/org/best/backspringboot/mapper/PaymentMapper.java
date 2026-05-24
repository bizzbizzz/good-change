package org.best.backspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.dto.payment.PaymentSearchDto;
import org.best.backspringboot.entity.Payment;
import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentMapper {
    List<Payment> findAll(PaymentSearchDto dto);
    long countAll(PaymentSearchDto dto);
    Optional<Payment> findById(Long paymentId);
    void updateStatusAndDate(@Param("paymentId") Long paymentId,
                             @Param("status") String status,
                             @Param("transmissionDate") String transmissionDate);
    void updateStatus(Long paymentId, String status);
    void insert(Payment payment);
    Optional<Payment> findByApprovalNumber(String approvalNumber);
    void delete(Long paymentId);
}