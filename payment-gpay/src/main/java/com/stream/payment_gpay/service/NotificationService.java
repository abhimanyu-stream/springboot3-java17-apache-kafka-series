package com.stream.payment_gpay.service;


import com.stream.payment_gpay.model.PayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    //Sends confirmation emails/SMS to customers
    //	Notification Service listens to the payment-transactions topic.
    //	Sends a confirmation to the user (via email/SMS).




    @Autowired
    private KafkaTemplate<String, PayRequest> kafkaTemplate;


    public NotificationService(KafkaTemplate<String, PayRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "payment-processed-topic", groupId = "payment-processed-group")
    public void paymentProcessing(PayRequest payRequest) {
        // Process authorization
        boolean result = authorizePaymentRequest(payRequest);

        // Send authorization result
        kafkaTemplate.send("payment-processed-topic", payRequest);
    }

    private boolean authorizePaymentRequest(PayRequest request) {
        // Authorization logic
        //call authentication-authorization-service
        return true;
    }
}
