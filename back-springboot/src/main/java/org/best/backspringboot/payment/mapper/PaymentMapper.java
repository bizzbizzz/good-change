package org.best.backspringboot.payment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.best.backspringboot.payment.dto.payment.PaymentSearchDto;
import org.best.backspringboot.payment.entity.Payment;
import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentMapper {
    List<Payment> findAll(PaymentSearchDto dto);
    long countAll(PaymentSearchDto dto);
    Optional<Payment> findById(@Param("paymentId") Long paymentId,
                               @Param("transmissionDate") String transmissionDate);
    void updateStatusAndDate(@Param("paymentId") Long paymentId,
                             @Param("status") String status,
                             @Param("transmissionDate") String transmissionDate);
    void insertCancel(Payment payment);
    void insert(Payment payment);
    Optional<Payment> findByApprovalNumber(String approvalNumber);
    void delete(@Param("paymentId") Long paymentId,
                @Param("transmissionDate") String transmissionDate);  // 추가
    long sumAmount(PaymentSearchDto dto);
}