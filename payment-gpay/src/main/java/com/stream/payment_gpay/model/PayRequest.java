package com.stream.payment_gpay.model;


import com.stream.payment_gpay.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Data
@Entity
@Table(name = "pay")

public class PayRequest {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// its PK
    private Long userId;//its User userId
    private String payRequestId;// UUID type
    private String transactionId;// UUID
    private Long sourceAccountId;
    private Long targetAccountId;
    private Double amount;
    private String location;
    private String deviceType;
    private String agent;// Browser or Apps installed in mobile device
    private String ipAddress;
    private String timestamp;
    private String paymentMethod;
    @Enumerated
    private TransactionStatus paymentStatus;

    public void setPaymentStatus(TransactionStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
