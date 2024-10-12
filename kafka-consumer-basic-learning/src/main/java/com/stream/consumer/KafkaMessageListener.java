package com.stream.consumer;

import com.stream.dto.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListener {

    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

    @KafkaListener(topics = "streamtopic1",groupId = "customer-consumer-group")
    public void consumeCustomerEvents(Customer customer) {
        log.info("consumer consume the events {} ", customer.toString());
    }

    @KafkaListener(topics = "streamtopic2",groupId = "message-consumer-group1")
    public void consumeMessageEventsByGroup1(String message) {
        log.info("consumer consume the events {} ", message);
    }
    @KafkaListener(topics = "streamtopic2",groupId = "message-consumer-group2")
    public void consumeMessageEventsByGroup2(String message) {
        log.info("consumer consume the events {} ", message);
    }

}
