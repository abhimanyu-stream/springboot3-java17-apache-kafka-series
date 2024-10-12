package com.stream.payment_gpay.enums;

public enum FraudRule {

    LARGE_TRANSACTION,             // Rule 1
    SUSPICIOUS_LOCATION,           // Rule 2
    MULTIPLE_TRANSACTIONS,         // Rule 3
    SUSPICIOUS_DEVICE_OR_IP,       // Rule 4
    HIGH_DECLINE_RATE,             // Rule 5
    MULTIPLE_GEOLOCATIONS,         // Rule 6
    UNUSUAL_TIME_OF_DAY,           // Rule 7
    PAYMENT_METHOD_MISMATCH,       // Rule 8
    VELOCITY_ON_AMOUNT,            // Rule 9
    BLACKLISTED_COUNTRY            // Rule 10

}
