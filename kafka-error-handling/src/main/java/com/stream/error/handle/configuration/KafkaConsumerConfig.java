package com.stream.error.handle.configuration;


import com.stream.error.handle.model.User;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
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



    private KafkaProperties kafkaProperties;// To go with default configuration we can use KafkaProperties
    @Autowired
    public KafkaConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"user-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.stream.error.handle.dto,com.stream.error.handle.model");// Only on Consumer side
        return props;
    }
    // reference for Configure beans :- https://thepracticaldeveloper.com/spring-boot-kafka-config/
    @Bean
    public ConsumerFactory<String, User> consumerUserFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean("kafkaListenerContainerUserFactory")
    public ConcurrentKafkaListenerContainerFactory<String, User> kafkaListenerContainerUserFactory(KafkaTemplate<String, User> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, User> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerUserFactory());
        factory.setReplyTemplate(kafkaTemplate);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        // Other customizations (e.g., concurrency, error handling) can be added here
        return factory;
    }


    /**
     * other type of consumer and kafkaListener are below
     * */

}






