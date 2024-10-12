package com.stream.payment_gpay.repository;

import com.stream.payment_gpay.model.PayRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PayRequestRepository extends JpaRepository<PayRequest, Long> {
}
