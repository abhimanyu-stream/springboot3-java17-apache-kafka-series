package com.stream.error.handle.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stream.error.handle.dto.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class KafkaMessageConsumer {

    // if attempts is not defined then kafka do 3 try
    //This method is the message handler for the Kafka topic.

    //UserRequest user: This parameter represents the message payload, which will be deserialized into a UserRequest object.

    //@Header(KafkaHeaders.RECEIVED_TOPIC) String topic: This parameter captures the Kafka topic from which the message was received. It's useful for logging and debugging purposes.

    //@Header(KafkaHeaders.OFFSET) long offset: This parameter captures the offset of the message in the Kafka topic. It's also useful for logging and tracking message processing.

    @RetryableTopic(attempts = "4")// 3 topic N-1
    @KafkaListener(topics = "${app.topic.name}", groupId = "user-consumer-group")
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
}
