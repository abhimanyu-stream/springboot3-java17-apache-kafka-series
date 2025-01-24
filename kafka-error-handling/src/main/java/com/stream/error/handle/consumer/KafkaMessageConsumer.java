package com.stream.error.handle.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stream.error.handle.dto.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class KafkaMessageConsumer {

    // if attempts is not defined then kafka do 3 try
    //This method is the message handler for the Kafka topic.

    //UserRequest user: This parameter represents the message payload, which will be deserialized into a UserRequest object.

    //@Header(KafkaHeaders.RECEIVED_TOPIC) String topic: This parameter captures the Kafka topic from which the message was received. It's useful for logging and debugging purposes.

    //@Header(KafkaHeaders.OFFSET) long offset: This parameter captures the offset of the message in the Kafka topic. It's also useful for logging and tracking message processing.


    @RetryableTopic(attempts = "4")// 3 topic N-1
    @KafkaListener(topics = "${app.topic.name}", groupId = "user-consumer-group",clientIdPrefix = "user-", containerFactory = "kafkaListenerContainerUserFactory")
    public void consumeEvents(UserRequest user, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("Received: {} from {} offset {}", new ObjectMapper().writeValueAsString(user), topic, offset);

            //validate restricted IP before process the records

            List<String> restrictedIpList = Stream.of("32.241.244.236", "15.55.49.164", "81.1.95.253", "126.130.43.183").collect(Collectors.toList());
            if (restrictedIpList.contains(user.getIpAddress())) {
                throw new RuntimeException("Invalid IP Address received !");
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @DltHandler
    public void listenDLT(UserRequest user, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("DLT Received : {} , from {} , offset {}",user.getFirstName(),topic,offset);
    }
    private static String typeIdHeader(Headers headers) {
        return StreamSupport.stream(headers.spliterator(), false)
                .filter(header -> header.key().equals("__TypeId__"))
                .findFirst().map(header -> new String(header.value())).orElse("N/A");
    }



    //Handling Retries with RetryableTopic
    //@RetryableTopic in Spring Kafka allows retries for failed messages in Kafka. This can be applied to the listener, specifying delay, max attempts, and a dlt (dead-letter topic) for unprocessed messages.
    @KafkaListener(topics = "kafka-error-handle", containerFactory = "kafkaListenerContainerUserFactory")
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 2000),
            dltTopicSuffix = "-dlt",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    public void listen(String message) {
        // Process the message here
    }

    @DltHandler
    public void handleDltMessage(String message,@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset, ListenerExecutionFailedException e) {
        // Handle messages that are sent to DLT after retries have been exhausted
        System.out.println("DLT message: " + message);
    }

    //Summary of Key Configuration
    //Idempotency and Transactions in Producer: Configure producer properties for enable.idempotence and set a transactional.id.
    //Consumer Offset Management: Use enable.auto.commit=false with a MANUAL AckMode.
    //Retryable Topics: Configure retry behavior with @RetryableTopic for automatic retry on processing failures.


}
