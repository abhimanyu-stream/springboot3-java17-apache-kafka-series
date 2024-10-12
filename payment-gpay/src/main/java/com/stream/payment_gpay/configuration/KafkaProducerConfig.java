package com.stream.payment_gpay.configuration;

import com.stream.payment_gpay.model.MoneyTransferEvent;
import com.stream.payment_gpay.model.PayRequest;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {


    @Bean
    public Map<String,Object> producerConfig(){
        String bootstrapServer = "127.0.0.1:9092";

        //Properties prop = new Properties();
        Map<String, Object> prop = new HashMap<>();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,JsonSerializer.class);


        // TO have a safe producer where the writes are acknowledged, we should use below settings
        prop.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        prop.put(ProducerConfig.ACKS_CONFIG	, "all");
        prop.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        prop.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        // To have a High throughput producer, we should use below settings
        prop.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        prop.put(ProducerConfig.LINGER_MS_CONFIG, "20");
        prop.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024)); //32KB
        prop.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,"tx-");


        return prop;
    }

    @Bean("producerFactoryPayRequest")
    public ProducerFactory<String,PayRequest> producerFactoryPayRequest(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }
    @Bean("producerFactoryMoneyTransferEvent")
    public ProducerFactory<String,MoneyTransferEvent> producerFactoryMoneyTransferEvent(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }


    @Bean("kafkaTemplateMoneyTransferEvent")
    public KafkaTemplate<String, MoneyTransferEvent> kafkaTemplateMoneyTransferEvent() {
        KafkaTemplate<String, MoneyTransferEvent> kafkaTemplate = new KafkaTemplate<>(producerFactoryMoneyTransferEvent());
        kafkaTemplate.setTransactionIdPrefix("tx-");
        return kafkaTemplate;
    }

    @Bean("kafkaTemplatePayRequest")
    public KafkaTemplate<String, PayRequest> kafkaTemplatePayRequest() {
        KafkaTemplate<String, PayRequest> kafkaTemplate = new KafkaTemplate<>(producerFactoryPayRequest());
        kafkaTemplate.setTransactionIdPrefix("tx-");
        return kafkaTemplate;
    }

    @Bean("kafkaTransactionManagerPayRequest")
    public KafkaTransactionManager<String, PayRequest> kafkaTransactionManagerPayRequest(ProducerFactory<String, PayRequest> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }
    @Bean("kafkaTransactionManagerMoneyTransferEvent")
    public KafkaTransactionManager<String, MoneyTransferEvent> kafkaTransactionManagerMoneyTransferEvent(ProducerFactory<String, MoneyTransferEvent> producerFactoryMoneyTransferEvent) {
        return new KafkaTransactionManager<>(producerFactoryMoneyTransferEvent);
    }

}
