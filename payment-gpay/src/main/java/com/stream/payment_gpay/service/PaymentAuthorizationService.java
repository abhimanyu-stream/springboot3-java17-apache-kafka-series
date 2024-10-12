package com.stream.payment_gpay.service;


import com.stream.payment_gpay.model.PayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentAuthorizationService {

    @Autowired
    private KafkaTemplate<String, PayRequest> kafkaTemplate;


    public PaymentAuthorizationService(KafkaTemplate<String, PayRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "payment-requests-topic", groupId = "payment-auth-group")
    public void listen(PayRequest payRequest) {
        // Process authorization
        //String result = authorizePayment(payRequest);

        // Send authorization result
        kafkaTemplate.send("payment-authorizations-topic", payRequest);
    }

   /* private PaymentAuthorizationResult authorizePayment(PayRequest request) {
        // Authorization logic
        return new PaymentAuthorizationResult(request.getId(), true); // Example result
    }*/
}
