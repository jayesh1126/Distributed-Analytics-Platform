package com.Events_service.config;

import com.Events_service.dto.EventResponseDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaProducerConfig is a configuration class that sets up the necessary beans for producing messages to Kafka topics in the application.
 * It includes the configuration for the ProducerFactory, which is responsible for creating Kafka producers, and the KafkaTemplate, which provides a high-level API for sending messages to Kafka topics.
 */
@Configuration
public class KafkaProducerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    /**
     * Configures the ProducerFactory bean, which is responsible for creating Kafka producers.
     * It sets the necessary configuration properties for connecting to the Kafka cluster and serializing the key and value of the messages.
     * @return a ProducerFactory object with the specified configuration
     */
    @Bean
    public ProducerFactory<String, EventResponseDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Configures the KafkaTemplate bean, which provides a high-level API for sending messages to Kafka topics.
     * It uses the ProducerFactory to create Kafka producers and allows for sending messages with a specified key and value.
     * @return a KafkaTemplate object that can be used to send messages to Kafka topics
     */
    @Bean
    public KafkaTemplate<String, EventResponseDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
