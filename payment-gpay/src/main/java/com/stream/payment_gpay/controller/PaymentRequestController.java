package com.stream.payment_gpay.controller;


import com.stream.payment_gpay.dto.PaymentRequest;
import com.stream.payment_gpay.dto.PaymentResponse;
import com.stream.payment_gpay.model.PayRequest;
import com.stream.payment_gpay.service.PaymentRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class PaymentRequestController {

    @Qualifier("KafkaTemplate")
    private final KafkaTemplate<String, PayRequest> kafkaTemplate;
    @Qualifier("kafkaTransactionManagerPayRequest")
    private final KafkaTransactionManager<String, PayRequest> kafkaTransactionManager;


    @Autowired
    private PaymentRequestService paymentRequestService;

    public PaymentRequestController(KafkaTemplate<String, PayRequest> kafkaTemplate, KafkaTransactionManager<String, PayRequest> kafkaTransactionManager) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTransactionManager = kafkaTransactionManager;

    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPaymentRequest(@RequestBody @Valid PaymentRequest paymentRequest) {
        // Validate data paymentRequest request logic



        String message = paymentRequestService.createPaymentRequest(paymentRequest);




        // we have to many other things here upto capturing payment response
        return ResponseEntity.ok(new PaymentResponse());
    }

}
