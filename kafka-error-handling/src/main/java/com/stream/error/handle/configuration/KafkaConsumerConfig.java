package com.stream.error.handle.configuration;


import com.stream.error.handle.model.User;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableKafka
public class KafkaConsumerConfig {


    /**
     * NOTES:-
     * @EnableKafka enables detection of KafkaListener annotations on any Spring-managed bean in the container. For example, given a class MyService:
     *
     *  package com.acme.foo;
     *
     *  public class MyService {
     *         @KafkaListener(containerFactory = "myKafkaListenerContainerFactory", topics = "myTopic")
     *         public void process(String msg) {
     *                 // process incoming message
     *         }
     *  }
     *
     *  Therefore it(@EnableKafka) should be mentioned on Consumer Configuration
     * */

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "user-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.stream.error.handle.dto,com.stream.error.handle.model");// Only on Consumer side
        return props;
    }
    // reference for Configure beans :- https://thepracticaldeveloper.com/spring-boot-kafka-config/
    @Bean
    public ConsumerFactory<String, User> consumerFactoryUser() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean("kafkaListenerContainerFactoryUser")
    public ConcurrentKafkaListenerContainerFactory<String, User> kafkaListenerContainerFactoryUser(KafkaTemplate<String, User> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, User> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryUser());
        factory.setReplyTemplate(kafkaTemplate);
        // Other customizations (e.g., concurrency, error handling) can be added here
        return factory;
    }


}






