package com.stream.payment_gpay.service;

import com.stream.payment_gpay.dto.PaymentRequest;
import com.stream.payment_gpay.model.PayRequest;
import com.stream.payment_gpay.repository.PayRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class PaymentRequestService {
    //Handles payment requests
    //A customer initiates a payment via a web or mobile application, which triggers a payment request to the Payment Service.
    //2.Kafka Event Creation: The Payment Service publishes the payment request event to a Kafka topic (e.g., payment-requests).

    //. High-Level Architecture
    //•	Client (Web/Mobile) → Payment Service → Kafka (Event Stream)
    //•	Kafka → Transaction Service → Kafka (Event Stream)
    //•	Kafka → Notification Service
    //•	Kafka → Audit Service

    @Qualifier("KafkaTemplate")
    private final KafkaTemplate<String, PayRequest> kafkaTemplate;
    private PayRequestRepository payRequestRepository;

    @Autowired
    public PaymentRequestService(KafkaTemplate<String, PayRequest> kafkaTemplate, PayRequestRepository payRequestRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.payRequestRepository = payRequestRepository;
    }

    public String createPaymentRequest(PaymentRequest paymentRequest) {

        // if ok then a PayRequest Object and send it kafka topic
        PayRequest payRequest = new PayRequest();
        payRequest.setUserId(paymentRequest.getUserId());//its User userId
        payRequest.setPayRequestId(UUID.randomUUID().toString());// UUID type
        payRequest.setPaymentMethod(paymentRequest.getPaymentMethod());
        payRequest.setSourceAccountId(paymentRequest.getSourceAccount());
        payRequest.setTargetAccountId(paymentRequest.getTargetAccount());
        payRequest.setAmount(paymentRequest.getAmount());
        payRequest.setLocation(paymentRequest.getLocation());
        payRequest.setDeviceType(paymentRequest.getLocation());
        payRequest.setAgent(paymentRequest.getAgent());// Browser or Apps installed in mobile device
        payRequest.setIpAddress(paymentRequest.getIpAddress());
        payRequest.setTimestamp(Timestamp.from(Instant.now()).toString());
        payRequest = payRequestRepository.save(payRequest);

        //kafkaTemplate.send("payment-requests-topic", payRequest);

        // topic-name, key, data
        CompletableFuture<SendResult<String, PayRequest>> result = kafkaTemplate.send("payment-requests-topic",payRequest.getPaymentMethod(), payRequest);
        result.whenComplete((sendResult, ex) ->
                log.debug("Sent(key={},partition={}): {}",
                        sendResult.getProducerRecord().partition(),
                        sendResult.getProducerRecord().key(),
                        sendResult.getProducerRecord().value()));
        System.out.println("Payment request created and published on Kafak topic payment-requests-topic");
        return "Payment request created and published on Kafak topic payment-requests-topic";
    }
}
