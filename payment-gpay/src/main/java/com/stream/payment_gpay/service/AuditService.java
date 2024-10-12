package com.stream.payment_gpay.service;


import org.springframework.stereotype.Service;

@Service
public class AuditService {

    // Logs all transactions for future reference.
    //	Audit Service listens to all relevant Kafka topics (payment-requests, payment-transactions).
    //	Logs the transactions in a database for auditing purposes.

    //@KafkaListener
}
