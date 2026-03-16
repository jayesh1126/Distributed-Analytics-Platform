package com.Orders_service.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

import static com.Orders_service.util.Constants.ORDERS_TOPIC;

/**
 * KafkaTopicConfig is a configuration class that sets up the necessary beans for managing Kafka topics in the application.
 * It includes the configuration for KafkaAdmin, which is responsible for administrative tasks related to Kafka, and defines a bean for creating a specific Kafka topic.
 */
@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    /**
     * Configures the KafkaAdmin bean, which is responsible for managing Kafka topics and other administrative tasks.
     * It sets the bootstrap servers configuration to connect to the Kafka cluster.
     * @return a KafkaAdmin object with the specified configuration
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    /**
     * Specifies the Kafka topic to be created if it doesn't exist.
     * In this case, we create a topic named "events" with 1 partition and a replication factor of 1.
     * @return a NewTopic object representing the Kafka topic configuration
     */
    @Bean
    public NewTopic eventsTopic() {
        return new NewTopic(ORDERS_TOPIC, 1, (short) 1);
    }
}
