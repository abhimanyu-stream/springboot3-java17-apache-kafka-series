package com.stream.payment_gpay.configuration;

import com.stream.payment_gpay.model.MoneyTransferEvent;
import com.stream.payment_gpay.model.PayRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "payment-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.stream.payment_gpay.dto,com.stream.payment_gpay.model");// Only on Consumer side
        return props;
    }

    @Bean
    public ConsumerFactory<String, PayRequest> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean("kafkaListenerContainerFactoryPayRequest")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryPayRequest(DefaultKafkaConsumerFactory<String, String> consumerFactory, KafkaTemplate<String, PayRequest> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);
        // Other customizations (e.g., concurrency, error handling) can be added here
        return factory;
    }
    @Bean
    public ConsumerFactory<String, MoneyTransferEvent> consumerFactoryMoneyTransferEvent(){
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean("kafkaListenerContainerFactoryPayRequestMoneyTransferEvent")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryPayRequestMoneyTransferEvent(DefaultKafkaConsumerFactory<String, String> consumerFactory, KafkaTemplate<String, MoneyTransferEvent> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);
        // Other customizations (e.g., concurrency, error handling) can be added here
        return factory;
    }




}
