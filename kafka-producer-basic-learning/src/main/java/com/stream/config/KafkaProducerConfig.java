package com.stream.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean("streamtopic1")
    public NewTopic createTopicStreamTopic1(){
        return new NewTopic("streamtopic1", 3, (short) 1);
    }
    @Bean("streamtopic2")
    public NewTopic createTopicStreamTopic2(){
        return new NewTopic("streamtopic2", 3, (short) 1);
    }
    //NewTopic(String name, int numPartitions, short replicationFactor)
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


        return prop;
    }

    @Bean
    public ProducerFactory<String,Object> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String,Object> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}
