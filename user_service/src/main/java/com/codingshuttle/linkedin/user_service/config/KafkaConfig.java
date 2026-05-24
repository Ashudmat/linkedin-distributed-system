package com.codingshuttle.linkedin.user_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic userCreated(){
        return new NewTopic("user_created_topic", 3, (short) 1);
    }

}
