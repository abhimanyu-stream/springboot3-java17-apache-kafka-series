package com.stream.payment_gpay.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

@Data
@Slf4j
@Builder
public class PaymentRequest implements Serializable {



    private Long userId;// User user
    private Long sourceAccount;
    private Long targetAccount;
    private Double amount;
    private String location;
    private String deviceType;
    private String agent;// Browser or Apps installed in mobile device
    private String ipAddress;
    private String paymentMethod;


    public PaymentRequest(){}

    public PaymentRequest(Long userId, Long sourceAccount, Long targetAccount, Double amount, String location, String deviceType, String agent, String ipAddress, String paymentMethod) {

        this.userId = userId;
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.location = location;
        this.deviceType = deviceType;
        this.agent = agent;
        this.ipAddress = ipAddress;
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "PayRequest{" +
                ", userId='" + userId + '\'' +
                ", sourceAccount=" + sourceAccount +
                ", targetAccount=" + targetAccount +
                ", amount=" + amount +
                ", location='" + location + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", agent='" + agent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
