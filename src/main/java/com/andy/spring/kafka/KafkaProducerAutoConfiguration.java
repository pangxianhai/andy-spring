package com.andy.spring.kafka;

import com.andy.spring.kafka.KafkaProperties.ProducerProperties;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * kafka自动配置
 *
 * @author 庞先海 2019-11-24
 */
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaProducerAutoConfiguration {

    @Bean
    public KafkaProducer<String, String> kafkaProducer(KafkaProperties properties) {
        ProducerProperties producerProperties = properties.getProducer();
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperties.getServers());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

}
