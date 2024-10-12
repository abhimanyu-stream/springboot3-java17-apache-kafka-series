package com.stream.error.handle.producer;

import com.stream.error.handle.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaMessageProducer {


    private KafkaTemplate<String, User> kafkaTemplate;
    @Autowired
    public KafkaMessageProducer(@Qualifier("kafkaTemplateUser") KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${app.topic.name}")
    private String topicName;



    public void sendEvents(User user) {
        try {
            CompletableFuture<SendResult<String, User>> future = kafkaTemplate.send(topicName, user);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message=[" + user.toString() +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");

                    System.out.println("Sent record to topic " + result.getRecordMetadata().topic() +
                               " partition " + result.getRecordMetadata().partition() +
                                " offset " + result.getRecordMetadata().offset() +
                                " timestamp " + result.getRecordMetadata().timestamp());
                    } else {
                    System.out.println("Unable to send message=[" + user.toString() + "] due to : " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


}
