package com.stream.payment_gpay.model;


import com.stream.payment_gpay.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class MoneyTransferEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// its PK
    private Long userId;//its User userId
    private String payRequestId;// UUID type
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;
    @Column(name = "source_account_id", nullable = false)
    private Long sourceAccountId;
    @Column(name = "target_account_id", nullable = false)
    private Long targetAccountId;
    @Column(name = "amount", nullable = false)
    private Double amount;
    private String paymentMethod;

    @Column(name = "transaction_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;
    @Column(name = "declined_count", nullable = false)
    private Integer isDeclinedCount;

    @Column(name = "description")
    private String description;


}
