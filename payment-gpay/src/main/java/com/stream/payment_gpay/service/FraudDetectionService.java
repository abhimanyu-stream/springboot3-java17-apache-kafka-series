package com.stream.payment_gpay.service;


import com.stream.payment_gpay.enums.FraudRule;
import com.stream.payment_gpay.model.PayRequest;
import com.stream.payment_gpay.repository.MoneyTransferEventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FraudDetectionService {



    private MoneyTransferEventRepository moneyTransferEventRepository;

    @Autowired
    private KafkaTemplate<String, PayRequest> kafkaTemplate;

    public FraudDetectionService(MoneyTransferEventRepository moneyTransferEventRepository, KafkaTemplate<String, PayRequest> kafkaTemplate) {
        this.moneyTransferEventRepository = moneyTransferEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final double LARGE_TRANSACTION_THRESHOLD = 10000;
    private static final List<String> BLACKLISTED_LOCATIONS = Arrays.asList("LocationX", "LocationY");
    private static final List<String> BLACKLISTED_COUNTRIES = Arrays.asList("CountryA", "CountryB");
    private static final List<String> BLACKLISTED_DEVICES = Arrays.asList("device1", "device2");
    private static final List<String> BLACKLISTED_IPS = Arrays.asList("192.168.1.1", "192.168.1.2");
    private static final List<String> SUSPICIOUS_PAYMENT_METHODS = Arrays.asList("PrepaidCard", "Bitcoin");

    private final Map<String, List<PayRequest>> userTransactionHistory = new HashMap<>(); // Used for velocity checks

    @Qualifier("concurrentKafkaListener")
    @KafkaListener(topics = "payment-authorizations-topic", groupId = "payment-fraud-group")

    void fraudDetection(PayRequest payRequest){

        Set<FraudRule> fraudRuleSet = detectFraud(payRequest);

        if (!(fraudRuleSet.isEmpty())) {
            System.out.println("Fraud detected for transaction: " + payRequest.getPayRequestId());

            // Publish fraud alert to a Kafka topic

            //Create a "fraud-alerts" topic and store meaning ful class data, similary in othns
            //kafkaTemplate.send("fraud-alerts", payRequest.getPaymentMethod(), payRequest);
        } else {
            System.out.println("No Fraud Found");
            kafkaTemplate.send("payment-fraud-checked-authorizations-checked-topic", payRequest.getPaymentMethod(), payRequest);
        }

    }



    // Apply all rules
    private Set<FraudRule> detectFraud(PayRequest payRequest) {
        Set<FraudRule> triggeredRules = new HashSet<>();


        // Rule 0.1: One way ssl or Two way ssl implementation

        // Rule 1: Large transaction amounts
        if (payRequest.getAmount() > LARGE_TRANSACTION_THRESHOLD) {
            triggeredRules.add(FraudRule.LARGE_TRANSACTION);
        }

        // Rule 2: Suspicious location
        if (BLACKLISTED_LOCATIONS.contains(payRequest.getLocation())) {
            triggeredRules.add(FraudRule.SUSPICIOUS_LOCATION);
        }

        // Rule 3: Multiple transactions in a short period
        if (isMultipleTransactions(payRequest)) {
            triggeredRules.add(FraudRule.MULTIPLE_TRANSACTIONS);
        }

        // Rule 4: Suspicious device or IP address
        if (BLACKLISTED_DEVICES.contains(payRequest.getDeviceType()) || BLACKLISTED_IPS.contains(payRequest.getIpAddress())) {
            //IP Address Check: Identify transactions coming from suspicious IP addresses.
            //Device Fingerprinting: Detect suspicious devices by checking the device ID or user agent.
            triggeredRules.add(FraudRule.SUSPICIOUS_DEVICE_OR_IP);
        }

        // Rule 5: High number of declined transactions
        if (isHighDeclineRate(payRequest)) {
            triggeredRules.add(FraudRule.HIGH_DECLINE_RATE);
        }

        // Rule 6: Transactions from multiple geolocations [ in least possible time duration ]
        if (isMultipleGeolocations(payRequest)) {
            triggeredRules.add(FraudRule.MULTIPLE_GEOLOCATIONS);
        }

        // Rule 7: Unusual time of day
        if (isUnusualTimeOfDay(payRequest)) {
            triggeredRules.add(FraudRule.UNUSUAL_TIME_OF_DAY);
        }

        // Rule 8: Payment method mismatch
        if (SUSPICIOUS_PAYMENT_METHODS.contains(payRequest.getPaymentMethod())) {
            triggeredRules.add(FraudRule.PAYMENT_METHOD_MISMATCH);
        }

        // Rule 9: Velocity check on transaction amount
        if (isVelocityOnAmount(payRequest)) {
            triggeredRules.add(FraudRule.VELOCITY_ON_AMOUNT);
        }

        // Rule 10: Blacklisted countries
        if (BLACKLISTED_COUNTRIES.contains(payRequest.getLocation())) {
            triggeredRules.add(FraudRule.BLACKLISTED_COUNTRY);
        }

        return triggeredRules; // Return all triggered fraud rules
    }
    private boolean isMultipleTransactions(PayRequest payRequest) {
        // Check if the user has made multiple transactions in a short time period

        //String timestampString = "2023-11-15 01:02:03";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(payRequest.getTimestamp(), formatter);

        LocalTime localTime = LocalTime.parse(payRequest.getTimestamp(), formatter);


        List<PayRequest> transactions = userTransactionHistory.getOrDefault(payRequest.getUserId(), new ArrayList<>());
        return transactions.size() >= 3 && transactions.stream().anyMatch(t -> getLocalTime(t.getTimestamp()).isAfter(getLocalTime(payRequest.getTimestamp()).minusMinutes(5)));
    }

    private LocalTime getLocalTime(String timestamp){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalTime localTime = LocalTime.parse(timestamp, formatter);
        return localTime;
    }



    private boolean isHighDeclineRate(PayRequest payRequest) {
        // Check if user has a high decline rate

        // Create a Repository for TransactionHistoryRepository
        // set declineCount there


        Integer isDeclinedCountFromDB = moneyTransferEventRepository.findByIsDeclinedCount(payRequest.getUserId());

        boolean declineCount = false;        

        if(isDeclinedCountFromDB >= 3){
            declineCount = true;
            return declineCount;
        }
        
        return declineCount; // Example rule: 3 declines within the last 10 minutes
    }

    private boolean isMultipleGeolocations(PayRequest payRequest) {
        // Check for transactions from multiple geolocations within a short period


        //String timestampString = "2023-11-15 01:02:03";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(payRequest.getTimestamp(), formatter);

        // Note:- You have improve logic in this methos
        List<PayRequest> transactions = userTransactionHistory.getOrDefault(payRequest.getUserId(), new ArrayList<>());
        return transactions.stream().anyMatch(t -> !t.getLocation().equals(payRequest.getLocation()) && localDateTime.isAfter(localDateTime.minusMinutes(5)));
    }

    private boolean isUnusualTimeOfDay(PayRequest payRequest) {

        //String timestampString = "2023-11-15 01:02:03";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalTime localDateTime = LocalTime.parse(payRequest.getTimestamp(), formatter);


        LocalTime transactionTime = localDateTime;
        return localDateTime.isAfter(LocalTime.of(0, 0)) && transactionTime.isBefore(LocalTime.of(4, 0)); // Flag transactions between 12 AM and 4 AM
    }

    private boolean isVelocityOnAmount(PayRequest payRequest) {
        // Check if there is a sudden spike in transaction amount
        List<PayRequest> transactions = userTransactionHistory.getOrDefault(payRequest.getUserId(), new ArrayList<>());
        return transactions.stream().anyMatch(t -> Math.abs(t.getAmount() - payRequest.getAmount()) > 5000); // Example rule
    }






}
/**
 * Extend the fraud detection logic with more complex rules:
 * •	Velocity Checks: Monitor the velocity of transactions, like the number of transactions in a specific time window.
 * •	Geolocation Check: Compare the transaction location with the user’s typical location.
 * •	IP Address Check: Identify transactions coming from suspicious IP addresses.
 * •	Device Fingerprinting: Detect suspicious devices by checking the device ID or user agent.
 *
 *
 * Rule 1: High Amount of Payment Request
 * Rule 2: isSuspiciousLocation
 * Rule 3: isTooFrequentTransactions
 * Rule 4: isSuspiciousDeviceOrIp
 *
 * Here are some additional rules you can implement:
 * Rule 5: High Number of Declined Transactions
 * •	If a user has had several declined transactions within a short time period, it could indicate a potential fraud attempt (e.g., brute force trying different cards).
 * Rule 6: Transactions From Multiple Geolocations in a Short Period
 * •	If a user initiates transactions from widely different geographic locations in a short time frame (impossible travel), it could indicate account takeover or a botnet attack.
 * Rule 7: Unusual Time of Day for Transactions
 * •	If a user normally performs transactions during the day and suddenly starts making large transactions in the middle of the night, it could be suspicious.
 * Rule 8: Payment Method Mismatch
 * •	If a user typically uses one payment method (e.g., credit card) and suddenly switches to a less secure method (e.g., a prepaid card), it might be suspicious.
 * Rule 9: Velocity Check on Transaction Amount
 * •	A sudden spike in transaction amounts within a short time period could indicate fraud. For example, a user who normally makes $100-$200 transactions suddenly makes a series of $5,000 transactions.
 * Rule 10: Transaction from Blacklisted Countries
 * •	If a transaction originates from a blacklisted country or region, it could be flagged as suspicious.
 * **/