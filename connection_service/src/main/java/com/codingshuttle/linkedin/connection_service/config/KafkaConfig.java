package com.codingshuttle.linkedin.connection_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic connectionRequestSent(){
        return new NewTopic("connection_request_sent_topic", 3, (short) 1);
    }

    @Bean
    public NewTopic connectionAccepted(){
        return new NewTopic("connection_accepted_topic", 3, (short) 1);
    }
}