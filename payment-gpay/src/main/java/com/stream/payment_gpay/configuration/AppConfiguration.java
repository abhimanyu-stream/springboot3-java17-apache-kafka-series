package com.stream.payment_gpay.configuration;

import com.stream.payment_gpay.util.UniqueTransactionIdGenerator;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AppConfiguration {


    @Bean("payment-transactions-topic")
    public NewTopic transactionsTopic() {
        return TopicBuilder.name("transactions-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean("payment-requests-topic")
    public NewTopic paymentRequestsTopic() {
        return TopicBuilder.name("payment-requests-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean("payment-authorizations-topic")
    public NewTopic paymentAuthorizationsTopic() {
        return TopicBuilder.name("payment-authorizations-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean("payment-fraud-checked-authorizations-checked-topic")
    public NewTopic paymentFraudTopic() {
        return TopicBuilder.name("payment-fraud-check-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean("payment-processed-topic")
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name("payment-processed-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean("payment-notification-topic")
    public NewTopic paymentNotificationTopic() {
        return TopicBuilder.name("payment-notification-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public UniqueTransactionIdGenerator uniqueTransactionIdGenerator(){

        return new UniqueTransactionIdGenerator();
    }
}
/***
 * payment-requests (where the PaymentService publishes validated payment requests)
 * payment-fraud-check (where the FraudDetectionService publishes fraud check results)
 * payment-authorizations (where the AuthorizationService publishes authorization results)
 * payment-processed-topic (where the PaymentGatewayService publishes the final payment status)
 * transactions-topic(where the transaction result will be stored)
 * payment-notification-topic (notification service sms email whatsapp will listen this topic and do its task)
 *
 *
 * */

