package com.stream.payment_gpay.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public  class UniqueTransactionIdGenerator {

    // Set to store previously generated unique transaction IDs
    private  Set<String> generatedTransactionIds = new HashSet<>();
    private  final Random random = new Random();

    public  String generateUniqueTransactionId() {
        String transactionId;
        do {
            // Generate a random 16-digit number using two 8-digit parts
            long part1 = 10000000L + random.nextInt(90000000); // First 8 digits
            long part2 = 10000000L + random.nextInt(90000000); // Second 8 digits
            transactionId = "tx-" + part1 + part2;
        } while (generatedTransactionIds.contains(transactionId)); // Check if it's unique

        // Add the generated ID to the set to ensure uniqueness
        generatedTransactionIds.add(transactionId);
        return transactionId;
    }
}
