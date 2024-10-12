package com.stream.payment_gpay.repository;

import com.stream.payment_gpay.model.MoneyTransferEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyTransferEventRepository extends JpaRepository<MoneyTransferEvent, Long> {
    Integer findByIsDeclinedCount(Long userId);
}
