package com.stream.payment_gpay.service;


import com.stream.payment_gpay.enums.TransactionStatus;
import com.stream.payment_gpay.model.Account;
import com.stream.payment_gpay.model.MoneyTransferEvent;
import com.stream.payment_gpay.model.PayRequest;
import com.stream.payment_gpay.repository.AccountRepository;
import com.stream.payment_gpay.repository.PayRequestRepository;
import com.stream.payment_gpay.util.UniqueTransactionIdGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PayementTransactionProcessingService {

    //Manages payment transactions, updates statuses, and processes payments
    // Transaction Processing
    //	Transaction Service listens to the payment-requests topic.
    //	It processes the payment (interacts with payment gateways, validates data, etc.).
    //	Once processed, it publishes a payment-processed event to a new Kafka topic (e.g., payment-transactions).


    @Autowired
    private KafkaTemplate<String, MoneyTransferEvent> kafkaTemplate;

    private AccountRepository accountRepository;
    private PayRequestRepository payRequestRepository;
    private UniqueTransactionIdGenerator uniqueTransactionIdGenerator;

    public PayementTransactionProcessingService(KafkaTemplate<String, MoneyTransferEvent> kafkaTemplate, AccountRepository accountRepository, PayRequestRepository payRequestRepository, UniqueTransactionIdGenerator uniqueTransactionIdGenerator) {
        this.kafkaTemplate = kafkaTemplate;
        this.accountRepository = accountRepository;
        this.payRequestRepository = payRequestRepository;
        this.uniqueTransactionIdGenerator = uniqueTransactionIdGenerator;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @KafkaListener(topics = "payment-fraud-checked-authorizations-checked-topic", groupId = "payment-processing-group")
    public void paymentProcessing(PayRequest payRequest) {


        //store some success failre value in payment-fraud-checked-authorizations-checked-topic and according go ahead


        // Process Payment call
        Account accountSource = accountRepository.findById(payRequest.getSourceAccountId()).orElseThrow();

        Account accountTarget = accountRepository.findById(payRequest.getTargetAccountId()).orElseThrow();

        if (accountSource.getBalance() >= payRequest.getAmount()) {// You can fine tune this line, like for min balance at bank
            accountSource.setBalance(accountSource.getBalance() - payRequest.getAmount());
            accountRepository.save(accountSource);// -
            accountTarget.setBalance(accountTarget.getBalance() + payRequest.getAmount());
            accountRepository.save(accountTarget);// +
            payRequest.setPaymentStatus(TransactionStatus.INITIATED);
            payRequest.setTransactionId(uniqueTransactionIdGenerator.generateUniqueTransactionId());
            payRequest = payRequestRepository.save(payRequest);// Transaction OutOfBox Pattern
            //Capture reponse and store it on topic payment-processed-topic
            //call Payment Gateway and capture response
            //payRequest.setPaymentStatus(TransactionStatus.COMPLETED);
            //payRequest.setPaymentStatus(TransactionStatus.FAILED);
            String result = "resultProcessed";


            final PayRequest finalPayRequest = payRequest;
            kafkaTemplate.executeInTransaction(operations -> {

                // Step 2: Publish an event to Kafka about the transaction


                MoneyTransferEvent moneyTransferEvent = new MoneyTransferEvent();
                moneyTransferEvent.setUserId(finalPayRequest.getUserId());
                moneyTransferEvent.setAmount(finalPayRequest.getAmount());
                moneyTransferEvent.setPaymentMethod(finalPayRequest.getPaymentMethod());
                moneyTransferEvent.setTransactionStatus(finalPayRequest.getPaymentStatus());
                moneyTransferEvent.setTransactionTime(LocalDateTime.now());
                moneyTransferEvent.setSourceAccountId(finalPayRequest.getSourceAccountId());
                moneyTransferEvent.setTargetAccountId(finalPayRequest.getTargetAccountId());
                moneyTransferEvent.setPayRequestId(finalPayRequest.getPayRequestId());
                moneyTransferEvent.setTransactionId(finalPayRequest.getTransactionId());
                operations.send("money-transfer-topic", moneyTransferEvent.getPaymentMethod(), moneyTransferEvent);
                operations.send("payment-processed-topic", moneyTransferEvent.getPaymentMethod(), moneyTransferEvent);
                operations.send("payment-notification-topic", moneyTransferEvent.getPaymentMethod(), moneyTransferEvent);
                operations.send("payment-transactions-topic", moneyTransferEvent.getPaymentMethod(), moneyTransferEvent);
                return true;
            });

        }else {
            // Insufficient fund
            // Payment fail
        }
    }
}

        /*



         kafkaTemplate.executeInTransaction(operations -> {

            // Step 2: Publish an event to Kafka about the transaction
//payment-processed-topic payment-notification-topic transactions-topic

            MoneyTransferEvent moneyTransferEvent = new MoneyTransferEvent();
            moneyTransferEvent.setUserId(finalPayRequest.getUserId());
            moneyTransferEvent.setAmount(finalPayRequest.getAmount());
            moneyTransferEvent.setPaymentMethod(finalPayRequest.getPaymentMethod());
            moneyTransferEvent.setTransactionStatus(finalPayRequest.getPaymentStatus());
            //Use Jpa adudit
            //moneyTransferEvent.setTransactionTime(LocalDateTime.now());
            moneyTransferEvent.setSourceAccountId(finalPayRequest.getSourceAccountId());
            moneyTransferEvent.setTargetAccountId(finalPayRequest.getTargetAccountId());
            moneyTransferEvent.setPayRequestId(finalPayRequest.getPayRequestId());
            moneyTransferEvent.setTransactionId(finalPayRequest.getTransactionId());
            operations.send("payment-processed-topic", moneyTransferEvent.getPaymentMethod(), moneyTransferEvent);
            operations.send("payment-notification-topic", moneyTransferEvent.getPaymentMethod(), moneyTransferEvent);
            operations.send("payment-transactions-topic", moneyTransferEvent.getPaymentMethod(), moneyTransferEvent);
            return true;
        });


        kafkaTemplate.executeInTransaction(operations -> {

            // Step 2: Publish an event to Kafka about the transaction


            MoneyTransferEvent moneyTransferEvent = new MoneyTransferEvent();
            moneyTransferEvent.setUserId(finalPayRequest.getUserId());
            moneyTransferEvent.setAmount(finalPayRequest.getAmount());
            moneyTransferEvent.setPaymentMethod(finalPayRequest.getPaymentMethod());
            moneyTransferEvent.setTransactionStatus(finalPayRequest.getPaymentStatus());
            moneyTransferEvent.setTransactionTime(LocalDateTime.now());
            moneyTransferEvent.setSourceAccountId(finalPayRequest.getSourceAccountId());
            moneyTransferEvent.setTargetAccountId(finalPayRequest.getTargetAccountId());
            moneyTransferEvent.setPayRequestId(finalPayRequest.getPayRequestId());
            moneyTransferEvent.setTransactionId(finalPayRequest.getTransactionId());
            operations.send("payment-notification-topic", moneyTransferEvent.getPaymentMethod(), moneyTransferEvent);
            operations.send("payment-transactions-topic", moneyTransferEvent.getPaymentMethod(), moneyTransferEvent);
            return true;
        });
        // Send
        kafkaTemplate.executeInTransaction(operations -> {

            // Step 2: Publish an event to Kafka about the transaction


            MoneyTransferEvent moneyTransferEvent = new MoneyTransferEvent();
            moneyTransferEvent.setUserId(finalPayRequest.getUserId());
            moneyTransferEvent.setAmount(finalPayRequest.getAmount());
            moneyTransferEvent.setPaymentMethod(finalPayRequest.getPaymentMethod());
            moneyTransferEvent.setTransactionStatus(finalPayRequest.getPaymentStatus());
            moneyTransferEvent.setTransactionTime(LocalDateTime.now());
            moneyTransferEvent.setSourceAccountId(finalPayRequest.getSourceAccountId());
            moneyTransferEvent.setTargetAccountId(finalPayRequest.getTargetAccountId());
            moneyTransferEvent.setPayRequestId(finalPayRequest.getPayRequestId());
            moneyTransferEvent.setTransactionId(finalPayRequest.getTransactionId());
            operations.send("payment-transactions-topic", moneyTransferEvent.getPaymentMethod(), moneyTransferEvent);
            return true;
        });*/

